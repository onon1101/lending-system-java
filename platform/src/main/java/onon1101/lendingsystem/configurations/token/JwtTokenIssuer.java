package onon1101.lendingsystem.configurations.token;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import onon1101.lendingsystem.configurations.time.IClock;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenIssuer {

    private final JwtEncoder jwtEncoder;
    private final IClock clock;

    public JwtTokenIssuer(JwtEncoder jwtEncoder, IClock clock) {
        this.jwtEncoder = jwtEncoder;
        this.clock = clock;
    }

    public String issue(
            UUID publicUserId, Map<String, Object> additionalClaims, TokenProperties properties) {
        Instant now = clock.now();

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
