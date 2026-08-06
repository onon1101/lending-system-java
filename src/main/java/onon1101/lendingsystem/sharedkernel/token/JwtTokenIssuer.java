package onon1101.lendingsystem.sharedkernel.token;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenIssuer {

    private final JwtEncoder jwtEncoder;

    public JwtTokenIssuer(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String issue(
            UUID publicUserId, Map<String, Object> additionalClaims, TokenProperties properties) {
        Instant now = Instant.now();

        JwtClaimsSet.Builder claims =
                JwtClaimsSet.builder()
                        .issuer(properties.issuer())
                        .issuedAt(now)
                        .expiresAt(now.plus(properties.expiration()))
                        .subject(publicUserId.toString())
                        .claim("purpose", properties.purpose());

        additionalClaims.forEach(claims::claim);

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();
    }
}
