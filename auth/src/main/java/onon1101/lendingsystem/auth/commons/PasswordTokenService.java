package onon1101.lendingsystem.auth.commons;

import java.util.UUID;
import onon1101.lendingsystem.configurations.token.JwtTokenService;
import onon1101.lendingsystem.configurations.token.TokenPayload;
import org.springframework.stereotype.Service;

@Service
public class PasswordTokenService {

    private final JwtTokenService tokenService;
    private final PasswordProperties properties;

    public PasswordTokenService(JwtTokenService tokenService, PasswordProperties properties) {
        this.properties = properties;
        this.tokenService = tokenService;
    }

    public String createToken(UUID publicUserId, String username) {
        return tokenService.encode(publicUserId, username, properties);
    }

    public TokenPayload decode(String token) {
        return tokenService.decode(token, properties);
    }
}
