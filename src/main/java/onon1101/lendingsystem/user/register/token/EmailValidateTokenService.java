package onon1101.lendingsystem.user.register.token;

import java.time.Instant;
import java.util.UUID;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class EmailValidateTokenService {

    private final JwtEncoder jwtEncoder;
    private final EmailValidateTokenProperties properties;

    public EmailValidateTokenService(
            JwtEncoder jwtEncoder, EmailValidateTokenProperties properties) {
        this.jwtEncoder = jwtEncoder;
        this.properties = properties;
    }

    public String createToken(UUID publicUserId, String username) {
        Instant now = Instant.now();
        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .issuer(properties.issuer())
                        .issuedAt(now)
                        .expiresAt(now.plus(properties.expiration()))
                        .subject(publicUserId.toString())
                        .claim("username", username)
                        .claim("purpose", "email-validation")
                        .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long expiresInSeconds() {
        return properties.expiration().toSeconds();
    }
}
