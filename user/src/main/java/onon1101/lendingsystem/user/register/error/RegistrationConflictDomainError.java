package onon1101.lendingsystem.user.register.error;

import onon1101.lendingsystem.shared.domain.DomainError;

public class RegistrationConflictDomainError extends DomainError {

    public RegistrationConflictDomainError() {
        super("User.RegistrationConflict", "The username or email is already in use.");
    }
}
