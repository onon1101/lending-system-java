package onon1101.lendingsystem.infrastructure.security.token;

import java.time.Duration;

public interface TokenProperties {

    String issuer();

    Duration expiration();

    String purpose();
}
