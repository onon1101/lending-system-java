package onon1101.lendingsystem.auth.login.token;

import java.util.Map;
import java.util.UUID;
import onon1101.lendingsystem.configurations.context.user.AccessTokenClaim;
import onon1101.lendingsystem.configurations.token.JwtTokenService;
import org.springframework.stereotype.Service;

@Service
public final class AccessTokenService {

    private final JwtTokenService tokenService;
    private final AccessTokenProperties properties;

    public AccessTokenService(JwtTokenService tokenService, AccessTokenProperties properties) {
        this.properties = properties;
        this.tokenService = tokenService;
    }

    public String createToken(long privateUserId, UUID publicUserId, String username) {
        return tokenService.encode(
                publicUserId,
                Map.of(AccessTokenClaim.USER_PRIVATE_ID, privateUserId, "username", username),
                properties);
    }

    public long expiresInSeconds() {
        return properties.expiration().toSeconds();
    }
}
