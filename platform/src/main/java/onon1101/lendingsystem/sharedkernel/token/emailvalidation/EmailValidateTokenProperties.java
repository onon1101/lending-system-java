package onon1101.lendingsystem.sharedkernel.token.emailvalidation;

import java.time.Duration;
import onon1101.lendingsystem.sharedkernel.token.TokenProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lending.email-token")
public record EmailValidateTokenProperties(String issuer, Duration expiration)
        implements TokenProperties {

    public static final String PURPOSE = "email-validation";

    @Override
    public String purpose() {
        return PURPOSE;
    }
}
