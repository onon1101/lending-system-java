package onon1101.lendingsystem.auth.login.error;

import onon1101.lendingsystem.sharedkernel.domain.result.DomainError;

public class TooManyAttemptsDomainError extends DomainError {
    public TooManyAttemptsDomainError() {
        super("Auth.TooManyAttempts", "This username try too many times.");
    }
}
