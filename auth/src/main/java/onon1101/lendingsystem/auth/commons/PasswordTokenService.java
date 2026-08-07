package onon1101.lendingsystem.auth.commons;

import java.time.Instant;
import java.util.UUID;

import onon1101.lendingsystem.sharedkernel.token.JwtDecoderProvider;
import onon1101.lendingsystem.sharedkernel.token.JwtTokenIssuer;

import onon1101.lendingsystem.sharedkernel.token.JwtTokenService;

import onon1101.lendingsystem.sharedkernel.token.TokenPayload;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordTokenService {

    private final JwtTokenService tokenService;
    private final PasswordProperties properties;

    public PasswordTokenService(
            JwtTokenService tokenService,
            PasswordProperties properties
    ) {
        this.properties = properties;
        this.tokenService = tokenService;
    }

    public String createToken(UUID publicUserId, String username) {
        return tokenService
                .encode(
                        publicUserId,
                        username,
                        properties);
    }

    public TokenPayload decode(
            String token
    ) {
        return tokenService
                .decode(token, properties);
    }
}
