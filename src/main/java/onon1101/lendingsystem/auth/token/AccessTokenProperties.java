package onon1101.lendingsystem.auth.token;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lending.access-token")
public record AccessTokenProperties(String secret, String issuer, Duration expiration) {}
