package onon1101.lendingsystem.auth.login.token;

import java.time.Duration;

import onon1101.lendingsystem.sharedkernel.token.TokenProperties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lending.access-token")
public record AccessTokenProperties(String issuer,
                                    Duration expiration) implements TokenProperties {

    public static final String PURPOSE = "access-token";

    @Override
    public String purpose() {
        return PURPOSE;
    }
}
