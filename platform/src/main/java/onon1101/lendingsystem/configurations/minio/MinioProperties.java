package onon1101.lendingsystem.configurations.minio;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lending.minio")
public record MinioProperties(
        boolean enabled, String endpoint, String accessKey, String secretKey, String bucket) {}
