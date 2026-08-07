package onon1101.lendingsystem.configurations.video;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "lending.video-downloader", name = "enabled", havingValue = "true")
public final class VideoDownloaderHealthIndicator implements HealthIndicator {

    private final HealthGrpc.HealthBlockingStub healthStub;

    public VideoDownloaderHealthIndicator(
            @Qualifier("videoDownloaderChannel") ManagedChannel videoDownloaderChannel) {
        this.healthStub = HealthGrpc.newBlockingStub(videoDownloaderChannel);
    }

    @Override
    public Health health() {
        try {
            HealthCheckResponse response =
                    healthStub
                            .withDeadlineAfter(2, TimeUnit.SECONDS)
                            .check(HealthCheckRequest.newBuilder().setService("").build());

            if (response.getStatus() != HealthCheckResponse.ServingStatus.SERVING) {
                return Health.down().withDetail("status", response.getStatus().name()).build();
            }

            return Health.up().build();
        } catch (StatusRuntimeException exception) {
            return Health.down()
                    .withDetail("grpcStatus", exception.getStatus().getCode().name())
                    .build();
        }
    }
}
