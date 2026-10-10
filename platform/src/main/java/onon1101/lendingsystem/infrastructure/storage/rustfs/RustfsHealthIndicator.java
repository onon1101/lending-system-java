package onon1101.lendingsystem.infrastructure.storage.rustfs;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
@ConditionalOnProperty(prefix = "lending.rustfs", name = "enabled", havingValue = "true")
public class RustfsHealthIndicator implements HealthIndicator {
    private final S3Client client;
    private final RustfsProperties properties;

    public RustfsHealthIndicator(S3Client client, RustfsProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    @Override
    public Health health() {
        try {
            client.headBucket(HeadBucketRequest.builder().bucket(properties.bucket()).build());
            return Health.up().withDetail("bucket", properties.bucket()).build();
        } catch (S3Exception exception) {
            return Health.down()
                    .withDetail(
                            "reason",
                            exception.statusCode() == 404
                                    ? "Required bucket does not exist"
                                    : "Bucket access failed")
                    .withDetail("status", exception.statusCode())
                    .withDetail("bucket", properties.bucket())
                    .build();
        } catch (Exception exception) {
            return Health.down().withDetail("reason", exception.getClass().getSimpleName()).build();
        }
    }
}
