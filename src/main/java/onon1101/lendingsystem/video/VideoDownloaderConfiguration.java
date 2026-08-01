package onon1101.lendingsystem.video;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "lending.video-downloader", name = "enabled", havingValue = "true")
public class VideoDownloaderConfiguration {

    @Bean(name = "videoDownloaderChannel", destroyMethod = "shutdown")
    ManagedChannel videoDownloaderChannel(VideoDownloaderProperties properties) {
        return ManagedChannelBuilder.forTarget(properties.target()).usePlaintext().build();
    }
}
