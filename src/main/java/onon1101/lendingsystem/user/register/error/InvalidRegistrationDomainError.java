package onon1101.lendingsystem.user.register.error;

import onon1101.lendingsystem.sharedkernel.domain.result.DomainError;

public class InvalidRegistrationDomainError extends DomainError {
    public InvalidRegistrationDomainError() {
        super("User.InvalidRegistration", "The User cannot be registered.");
    }
}
