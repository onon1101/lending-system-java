package onon1101.lendingsystem.auth.forgotPassword.token;

import java.time.Instant;
import java.util.UUID;

import onon1101.lendingsystem.auth.token.JwtTokenIssuer;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class ForgotPasswordTokenService {

    private final JwtTokenIssuer tokenIssuer;
    private final ForgotPasswordProperties properties;

    public ForgotPasswordTokenService(
            JwtTokenIssuer tokenIssuer,
            ForgotPasswordProperties properties
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
