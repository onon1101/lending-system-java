package onon1101.lendingsystem.auth.login.token;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lending.access-token")
public record AccessTokenProperties(String issuer, Duration expiration) {}
