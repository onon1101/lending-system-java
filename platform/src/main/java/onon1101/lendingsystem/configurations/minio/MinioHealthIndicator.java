package onon1101.lendingsystem.configurations.minio;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "lending.minio", name = "enabled", havingValue = "true")
public final class MinioHealthIndicator implements HealthIndicator {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public MinioHealthIndicator(MinioClient minioClient, MinioProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    @Override
    public Health health() {
        try {
            boolean bucketExists =
                    minioClient.bucketExists(
                            BucketExistsArgs.builder().bucket(properties.bucket()).build());

            if (!bucketExists) {
                return Health.down()
                        .withDetail("reason", "Required bucket does not exist")
                        .withDetail("bucket", properties.bucket())
                        .build();
            }

            return Health.up().withDetail("bucket", properties.bucket()).build();
        } catch (Exception exception) {
            return Health.down().withDetail("reason", exception.getClass().getSimpleName()).build();
        }
    }
}
