package onon1101.lendingsystem.user.commons;

import java.util.UUID;

import onon1101.lendingsystem.sharedkernel.token.JwtTokenIssuer;

import onon1101.lendingsystem.sharedkernel.token.JwtTokenService;

import onon1101.lendingsystem.sharedkernel.token.TokenPayload;

import org.springframework.stereotype.Service;

@Service
public class EmailValidateTokenService {

    private final JwtTokenService tokenService;
    private final EmailValidateTokenProperties properties;

    public EmailValidateTokenService(
            JwtTokenService tokenIssuer,
            EmailValidateTokenProperties properties
    ) {
        this.tokenService = tokenIssuer;
        this.properties = properties;
    }

    public String createToken(UUID publicUserId, String username) {
        return tokenService
                .encode(
                        publicUserId,
                        username,
                        properties
                );
    }

    public TokenPayload decode(
            String token
    ) {
        return tokenService
                .decode(token, properties);
    }
}
