package onon1101.lendingsystem.sharedkernel.token;

import java.time.Duration;

public interface TokenProperties {

    String issuer();

    Duration expiration();

    String purpose();
}
