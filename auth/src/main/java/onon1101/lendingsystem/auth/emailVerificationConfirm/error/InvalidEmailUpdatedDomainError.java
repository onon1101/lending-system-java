package onon1101.lendingsystem.auth.emailVerificationConfirm.error;

import onon1101.lendingsystem.shared.domain.DomainError;

public class InvalidEmailUpdatedDomainError extends DomainError {
    public InvalidEmailUpdatedDomainError() {
        super("User.InvalidEmailUpdated", "The updated status of email cannot be executed.");
    }
}
