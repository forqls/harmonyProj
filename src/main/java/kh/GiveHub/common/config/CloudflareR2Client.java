package kh.GiveHub.common.config;

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
        try{
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
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

    public void moveImage(String key){
        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                .sourceBucket("gh-temp")
                .sourceKey(key)
                .destinationBucket("gh-uploads")
                .destinationKey(key)
                .build();

        s3Client.copyObject(copyRequest);

    }

//    public String generateUploadUrl(String bucket, String key) {
//        try {
//            PutObjectRequest request = PutObjectRequest.builder()
//                    .bucket(bucket)
//                    .key(key)
//                    .build();
//
//            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
//                    .signatureDuration(Duration.ofMinutes(10))
//                    .putObjectRequest(request)
//                    .build();
//
//            String url = s3Presigner.presignPutObject(presignRequest).url().toString();
//            System.out.println("Generated PreSigned URL: " + url);
//            return url;
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to generate presigned URL: " + e.getMessage(), e);
//        }
//    }

}
