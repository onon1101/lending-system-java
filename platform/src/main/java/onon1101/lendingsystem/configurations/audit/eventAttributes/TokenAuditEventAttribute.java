package onon1101.lendingsystem.configurations.audit.eventAttributes;

import java.util.Objects;

import onon1101.lendingsystem.configurations.token.JwtTokenService;
import onon1101.lendingsystem.configurations.token.TokenPayload;
import onon1101.lendingsystem.configurations.token.TokenProperties;

public class TokenAuditEventAttribute implements AuditEventAttribute {

    private final String value;

    public TokenAuditEventAttribute(
            JwtTokenService tokenService, TokenProperties properties, String token) {
        Objects.requireNonNull(tokenService, "Token service must not be null.");
        Objects.requireNonNull(properties, "Token properties must not be null.");
        this.value = publicUserIdRef(tokenService, properties, token);
    }

    @Override
    public String Key() {
        return "publicUserIdRef";
    }

    @Override
    public String Value() {
        return value;
    }

    private String publicUserIdRef(
            JwtTokenService tokenService, TokenProperties properties, String token) {
        try {
            TokenPayload payload = tokenService.decode(token, properties);
            return payload.publicUserId().toString();
        } catch (RuntimeException exception) {
            return "unavailable";
        }
    }
}
