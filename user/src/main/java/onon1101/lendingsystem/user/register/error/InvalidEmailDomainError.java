package onon1101.lendingsystem.user.register.error;

import onon1101.lendingsystem.shared.domain.DomainError;

public class InvalidEmailDomainError extends DomainError {
    public InvalidEmailDomainError() {
        super("User.InvalidEmail", "The Invalid Email Address.");
    }
}
