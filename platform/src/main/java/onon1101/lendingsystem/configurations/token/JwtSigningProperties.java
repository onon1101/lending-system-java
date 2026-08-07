package onon1101.lendingsystem.configurations.token;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lending.jwt")
public record JwtSigningProperties(String secret) {}
