package kh.GiveHub.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class S3Config {
    @Value("${cloudflare.r2.account.id}")
    private String accountId;
    @Value("${cloudflare.r2.access.key}")
    private String accessKey;
    @Value("${cloudflare.r2.secret.key}")
    private String secretKey;
    @Value("${cloud.aws.r2.endpoint}")
    private String r2Endpoint;

    @Bean
    public S3Client buildS3Client(){

        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                accessKey, secretKey
        );

        S3Configuration serviceConfig = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .chunkedEncodingEnabled(false)
                .build();

        // r2Endpoint 필드의 값을 사용하여 URI를 생성합니다.
        return S3Client.builder()
                .endpointOverride(URI.create(r2Endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of("auto"))
                .serviceConfiguration(serviceConfig)
                .build();
    }

    @Bean
    public URI r2EndpointUri() throws URISyntaxException {
        return new URI(r2Endpoint);
    }

}
