package onon1101.lendingsystem.user.register.error;

import onon1101.lendingsystem.sharedkernel.domain.result.DomainError;

public class InvalidEmailDomainError extends DomainError {
    public InvalidEmailDomainError() {
        super("User.InvalidEmail", "The Invalid Email Address.");
    }
}
