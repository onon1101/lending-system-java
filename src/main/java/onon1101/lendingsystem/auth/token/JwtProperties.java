package onon1101.lendingsystem.auth.token;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lending.jwt")
public record JwtProperties(String secret, String issuer, Duration expiration) {}
