package onon1101.lendingsystem.user.validateEmail.error;

import onon1101.lendingsystem.sharedkernel.domain.result.DomainError;

public class InvalidEmailUpdatedDomainError extends DomainError {
    public InvalidEmailUpdatedDomainError() {
        super("User.InvalidEmailUpdated", "The updated status of email cannot be executed.");
    }
}
