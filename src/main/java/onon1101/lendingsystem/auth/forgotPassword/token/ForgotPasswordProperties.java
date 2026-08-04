package onon1101.lendingsystem.auth.forgotPassword.token;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lending.forgot-token")
public record ForgotPasswordProperties(String issuer, Duration expiration) {}
