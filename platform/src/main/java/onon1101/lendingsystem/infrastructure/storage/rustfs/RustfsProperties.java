package onon1101.lendingsystem.infrastructure.storage.rustfs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lending.rustfs")
public record RustfsProperties(
        boolean enabled,
        String endpoint,
        String accessKey,
        String secretKey,
        String bucket,
        String region) {}
