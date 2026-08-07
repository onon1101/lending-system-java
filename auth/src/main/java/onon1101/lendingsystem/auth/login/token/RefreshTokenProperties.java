package onon1101.lendingsystem.auth.login.token;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "lending.refresh-token")
public record RefreshTokenProperties(Duration expiration) {

    public RefreshTokenProperties {
        if (expiration == null ||
                expiration.isZero() ||
                expiration.isNegative()) {
            throw new IllegalStateException(
                    "Refresh token expiration must be positive"
            );
        }
    }
}
