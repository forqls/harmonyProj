package kh.GiveHub.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.List;


@Component
public class CloudflareR2Client {
    private final S3Client s3Client;
//    private final S3Presigner s3Presigner;

    //버키 이름 주입
    @Value("${R2_TEMP_BUCKET}")
    private String tempBucket;

    @Value("${cloudflare.r2.upload.bucket}")
    private String uploadBucket;

    @Value("${cloud.aws.r2.public-domain}")
    private String publicDomain;


    public CloudflareR2Client(S3Client s3Client){
        this.s3Client = s3Client;
//        this.s3Presigner = s3Presigner;
    }

    public List<Bucket> listBuckets() {
        try {
            return s3Client.listBuckets().buckets();
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to list buckets: " + e.getMessage(), e);
        }
    }

    public List<S3Object> listObjects(String bucketName) {
        try {
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            return s3Client.listObjectsV2(request).contents();
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to list objects in bucket " + bucketName + ": " + e.getMessage(), e);
        }
    }

    public void uploadImage(String bucket, String key, byte[] data){
        System.out.println("=== Upload Debug Info ===");
        System.out.println("Bucket: " + bucket);
        System.out.println("Key: " + key);
        System.out.println("Data length: " + data.length);

        // 1) 확장자 기반으로 contentType 추정
        String contentType = "application/octet-stream";
        String lower = key.toLowerCase();
        if (lower.endsWith(".png"))  contentType = "image/png";
        else if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) contentType = "image/jpeg";
        else if (lower.endsWith(".gif")) contentType = "image/gif";
        else if (lower.endsWith(".webp")) contentType = "image/webp";

        try{
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .contentDisposition("inline")
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(data));
        } catch (S3Exception e){
            System.out.println("=== Error Details ===");
            System.out.println("Status Code: " + e.statusCode());
            System.out.println("Error Code: " + e.awsErrorDetails().errorCode());
            System.out.println("Error Message: " + e.awsErrorDetails().errorMessage());
            System.out.println("Request ID: " + e.requestId());
            throw new RuntimeException("Failed to upload image"+e.getMessage());
        }
    }


    public String moveImage(String key){
        // 1) 최종 키는 선두 'T' 제거 (있을 때만)
        String destKey = key.startsWith("T") ? key.substring(1) : key;
        System.out.println("Copying from tempBucket: " + tempBucket + "/" + key + " → " + uploadBucket + "/" + destKey);

        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                .sourceBucket(tempBucket)
                .sourceKey(key)
                .destinationBucket(uploadBucket)
                .destinationKey(destKey)
                // ✅ 메타데이터 교체로 MIME 보정 (필요 시)
                .metadataDirective(MetadataDirective.REPLACE)
                .contentType(guessContentType(destKey))
                .contentDisposition("inline")
                .build();
        s3Client.copyObject(copyRequest);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(tempBucket)
                .key(key)
                .build();
        s3Client.deleteObject(deleteRequest);


        return destKey; // ✅ 최종 키 반환
    }

    private String guessContentType(String key){
        String k = key.toLowerCase();
        if (k.endsWith(".png"))  return "image/png";
        if (k.endsWith(".jpg") || k.endsWith(".jpeg")) return "image/jpeg";
        if (k.endsWith(".gif")) return "image/gif";
        if (k.endsWith(".webp")) return "image/webp";
        return "application/octet-stream";
    }

    public String getPublicUrl(String objectKey) {
        if (publicDomain == null || objectKey == null) {
            return null;
        }

        String baseUrl = publicDomain;

        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        String key = objectKey.startsWith("/") ? objectKey.substring(1) : objectKey;

        return baseUrl + key;
    }

}
