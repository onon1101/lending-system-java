package onon1101.lendingsystem.configurations.token.emailvalidation;

import java.util.UUID;
import onon1101.lendingsystem.configurations.token.JwtTokenService;
import onon1101.lendingsystem.configurations.token.TokenPayload;
import org.springframework.stereotype.Service;

@Service
public class EmailValidateTokenService {

    private final JwtTokenService tokenService;
    private final EmailValidateTokenProperties properties;

    public EmailValidateTokenService(
            JwtTokenService tokenService, EmailValidateTokenProperties properties) {
        this.tokenService = tokenService;
        this.properties = properties;
    }

    public String createToken(UUID publicUserId, String username) {
        return tokenService.encode(publicUserId, username, properties);
    }

    public TokenPayload decode(String token) {
        return tokenService.decode(token, properties);
    }
}
