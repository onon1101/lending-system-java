package onon1101.lendingsystem.configurations.token;

import javax.crypto.SecretKey;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class JwtDecoderFactory {

    private final SecretKey secretKey;

    public JwtDecoderFactory(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public JwtDecoder create(TokenProperties properties) {
        NimbusJwtDecoder decoder =
                NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();

        OAuth2TokenValidator<Jwt> issuerValidator =
                JwtValidators.createDefaultWithIssuer(properties.issuer());

        OAuth2TokenValidator<Jwt> purposeValidator =
                new JwtClaimValidator<>("purpose", properties.purpose()::equals);

        decoder.setJwtValidator(
                new DelegatingOAuth2TokenValidator<>(issuerValidator, purposeValidator));

        return decoder;
    }
}
