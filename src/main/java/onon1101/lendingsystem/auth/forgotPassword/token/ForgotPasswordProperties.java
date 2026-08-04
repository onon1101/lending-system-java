package onon1101.lendingsystem.auth.forgotPassword.token;

import java.time.Duration;

import onon1101.lendingsystem.auth.token.TokenProperties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lending.forgot-token")
public record ForgotPasswordProperties(String issuer,
                                       Duration expiration) implements TokenProperties {

    public static final String PURPOSE = "password-reset";

    @Override
    public String purpose() {
        return PURPOSE;
    }
}
