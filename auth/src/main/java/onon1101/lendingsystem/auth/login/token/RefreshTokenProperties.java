package onon1101.lendingsystem.auth.login.token;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lending.refresh-token")
public record RefreshTokenProperties(Duration expiration) {

    public RefreshTokenProperties {
        if (expiration == null || expiration.isZero() || expiration.isNegative()) {
            throw new IllegalStateException("Refresh token expiration must be positive");
        }
    }
}
