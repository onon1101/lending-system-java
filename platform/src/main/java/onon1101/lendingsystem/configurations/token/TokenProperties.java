package onon1101.lendingsystem.configurations.token;

import java.time.Duration;

public interface TokenProperties {

    String issuer();

    Duration expiration();

    String purpose();
}
