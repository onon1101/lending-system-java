package onon1101.lendingsystem.auth.commons;

import java.time.Instant;
import java.util.UUID;

import onon1101.lendingsystem.sharedkernel.token.JwtDecoderProvider;
import onon1101.lendingsystem.sharedkernel.token.JwtTokenIssuer;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordTokenService {

    private final JwtTokenIssuer tokenIssuer;
    private final JwtDecoder tokenDecoder;
    private final PasswordProperties properties;

    public PasswordTokenService(
            JwtTokenIssuer tokenIssuer,
            JwtDecoderProvider provider,
            PasswordProperties properties
    ) {
        this.tokenIssuer = tokenIssuer;
        this.properties = properties;
        this.tokenDecoder = provider.getDecoder(properties.purpose());
    }



    public String createToken(UUID publicUserId, String username) {
        return tokenIssuer
                .issue(publicUserId, username, properties);
    }

    public long expiresInSeconds() {
        return properties.expiration().toSeconds();
    }

    public TokenPayload decode(
            String token
    ) {
        Jwt jwt = tokenDecoder.decode(token);

        Instant issuedAt = jwt.getIssuedAt();

        if (issuedAt == null) {
            throw new IllegalArgumentException(
                    "Reset token does not contain issued-at");
        }

        String subject = jwt.getSubject();
        if (subject == null) {
            throw new IllegalStateException("The token subject cannot be retrieved.");
        }

        return new TokenPayload(
                UUID.fromString(jwt.getSubject()),
                issuedAt);
    }
}
