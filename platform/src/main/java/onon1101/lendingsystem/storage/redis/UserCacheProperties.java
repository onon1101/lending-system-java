package onon1101.lendingsystem.storage.redis;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lending.user-cache")
public record UserCacheProperties(Duration ttl) {

    public UserCacheProperties {
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            throw new IllegalArgumentException("lending.user-cache.ttl must be positive");
        }
    }
}
