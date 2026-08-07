package onon1101.lendingsystem.auth.commons;

import java.time.Duration;
import onon1101.lendingsystem.sharedkernel.token.TokenProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lending.forgot-token")
public record PasswordProperties(String issuer, Duration expiration) implements TokenProperties {

    public static final String PURPOSE = "password-reset";

    @Override
    public String purpose() {
        return PURPOSE;
    }
}
