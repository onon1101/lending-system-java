package onon1101.lendingsystem.user.register.token;

import java.util.UUID;

import onon1101.lendingsystem.sharedkernel.token.JwtTokenIssuer;

import org.springframework.stereotype.Service;

@Service
public class EmailValidateTokenService {

    private final JwtTokenIssuer tokenIssuer;
    private final EmailValidateTokenProperties properties;

    public EmailValidateTokenService(
            JwtTokenIssuer tokenIssuer,
            EmailValidateTokenProperties properties
    ) {
        this.tokenIssuer = tokenIssuer;
        this.properties = properties;
    }

    public String createToken(UUID publicUserId, String username) {
        return tokenIssuer
                .issue(publicUserId, username, properties);
    }

    public long expiresInSeconds() {
        return properties.expiration().toSeconds();
    }
}
