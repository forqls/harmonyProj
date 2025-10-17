package kh.GiveHub.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;


@Component
public class CloudflareR2Client {

    private final S3Client s3Client;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    @Value("${cloudflare.r2.public.url}")
    private String r2PublicUrl;
    private String key;


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

    public void uploadImage(String key, byte[] data){
        System.out.println("=== Upload Debug Info ===");
        System.out.println("Bucket: " + this.bucket);
        System.out.println("Key: " + key);
        System.out.println("Data length: " + data.length);

        String contentType = guessContentType(key);

        try{
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(this.bucket) // 항상 단일 버킷을 사용
                    .key(key)
                    .contentType(contentType)
                    .contentDisposition("inline")
                    .acl(ObjectCannedACL.PUBLIC_READ) // ★★★ 업로드 시 바로 공개 설정
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

    private String guessContentType(String key){
        String k = key.toLowerCase();
        if (k.endsWith(".png"))  return "image/png";
        if (k.endsWith(".jpg") || k.endsWith(".jpeg")) return "image/jpeg";
        if (k.endsWith(".gif")) return "image/gif";
        if (k.endsWith(".webp")) return "image/webp";
        return "application/octet-stream";
    }

    public String getPublicUrl(String objectKey) {
        if (r2PublicUrl == null || objectKey == null) {
            return null;
        }

        String baseUrl = r2PublicUrl;

        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        String key = objectKey.startsWith("/") ? objectKey.substring(1) : objectKey;

        return baseUrl + key;
    }

}
