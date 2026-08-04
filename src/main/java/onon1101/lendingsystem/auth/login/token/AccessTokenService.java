package onon1101.lendingsystem.auth.login.token;

import java.util.UUID;

import onon1101.lendingsystem.sharedkernel.token.JwtTokenIssuer;

import onon1101.lendingsystem.sharedkernel.token.JwtTokenService;

import org.springframework.stereotype.Service;

@Service
public final class AccessTokenService {

    private final JwtTokenService tokenService;
    private final AccessTokenProperties properties;

    public AccessTokenService(
            JwtTokenService tokenService,
            AccessTokenProperties properties
    ) {
        this.properties = properties;
        this.tokenService = tokenService;
    }

    public String createToken(UUID publicUserId, String username, String email) {
        return tokenService.encode(
                publicUserId,
                username,
                properties);
    }

    public long expiresInSeconds() {
        return properties
                .expiration()
                .toSeconds();
    }
}
