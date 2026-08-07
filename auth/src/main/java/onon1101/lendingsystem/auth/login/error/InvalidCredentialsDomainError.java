package onon1101.lendingsystem.auth.login.error;

import onon1101.lendingsystem.configurations.domain.DomainError;

public class InvalidCredentialsDomainError extends DomainError {

    public InvalidCredentialsDomainError() {
        super("Auth.InvalidCredentials", "Username or password is incorrect.");
    }
}
