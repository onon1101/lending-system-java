package onon1101.lendingsystem.infrastructure.storage.rustfs;

import java.net.URI;
import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "lending.rustfs", name = "enabled", havingValue = "true")
public class RustfsConfiguration {
    @Bean(destroyMethod = "close")
    S3Client rustfsClient(RustfsProperties properties) {
        return S3Client.builder()
                .endpointOverride(URI.create(properties.endpoint()))
                .region(Region.of(properties.region()))
                .credentialsProvider(credentials(properties))
                .forcePathStyle(true)
                .overrideConfiguration(
                        builder ->
                                builder.apiCallTimeout(Duration.ofSeconds(30))
                                        .apiCallAttemptTimeout(Duration.ofSeconds(10)))
                .build();
    }

    @Bean(destroyMethod = "close")
    S3Presigner rustfsPresigner(RustfsProperties properties) {
        return S3Presigner.builder()
                .endpointOverride(URI.create(properties.endpoint()))
                .region(Region.of(properties.region()))
                .credentialsProvider(credentials(properties))
                .serviceConfiguration(
                        S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }

    private static StaticCredentialsProvider credentials(RustfsProperties properties) {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(properties.accessKey(), properties.secretKey()));
    }
}
