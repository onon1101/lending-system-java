package onon1101.lendingsystem.auth.login.error;

import onon1101.lendingsystem.sharedkernel.domain.result.DomainError;

public class InvalidCredentialsDomainError extends DomainError {

    public InvalidCredentialsDomainError() {
        super("Auth.InvalidCredentials", "Username or password is incorrect.");
    }
}
