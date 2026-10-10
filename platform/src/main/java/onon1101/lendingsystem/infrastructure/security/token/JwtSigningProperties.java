package onon1101.lendingsystem.infrastructure.security.token;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lending.jwt")
public record JwtSigningProperties(String secret) {}
