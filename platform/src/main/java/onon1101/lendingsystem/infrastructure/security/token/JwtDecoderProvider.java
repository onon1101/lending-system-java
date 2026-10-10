package onon1101.lendingsystem.infrastructure.security.token;

import org.springframework.security.oauth2.jwt.JwtDecoder;

public interface JwtDecoderProvider {
    JwtDecoder getDecoder(String purpose);
}
