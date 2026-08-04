package onon1101.lendingsystem.sharedkernel.token;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenService {

    private final JwtTokenIssuer tokenIssuer;
    private final JwtDecoderProvider decoderProvider;

    public JwtTokenService(JwtTokenIssuer tokenIssuer, JwtDecoderProvider decoderProvider) {
        this.tokenIssuer = tokenIssuer;
        this.decoderProvider = decoderProvider;
    }

    public String encode(UUID publicUserid, String username, TokenProperties properties) {
        return tokenIssuer.issue(publicUserid, Map.of("username", username), properties);
    }

    public String encode(
            UUID publicUserId, Map<String, Object> additionalClaims, TokenProperties properties) {
        return tokenIssuer.issue(publicUserId, additionalClaims, properties);
    }

    public TokenPayload decode(String token, TokenProperties properties) {
        JwtDecoder decoder = decoderProvider.getDecoder(properties.purpose());

        Jwt jwt = decoder.decode(token);

        Instant issuedAt = jwt.getIssuedAt();
        if (issuedAt == null) {
            throw new IllegalArgumentException("Token does not contain issued-at");
        }

        String subject = jwt.getSubject();
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Token does not contain subject");
        }

        UUID publicUserId;
        try {
            publicUserId = UUID.fromString(subject);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Token subject is not a valid UUID", exception);
        }

        return new TokenPayload(publicUserId, issuedAt);
    }
}
