package onon1101.lendingsystem.auth.forgotPassword.error;

import onon1101.lendingsystem.sharedkernel.domain.result.DomainError;

public class InvalidEmailDomainError extends DomainError {
    public InvalidEmailDomainError() {
        super("ForgotPassword.InvalidEmail", "The provided email address is invalid");
    }
}
