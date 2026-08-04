package onon1101.lendingsystem.auth.resetPassword.error;

import onon1101.lendingsystem.sharedkernel.domain.result.DomainError;

public final class InvalidResetTokenDomainError extends DomainError {
    public InvalidResetTokenDomainError() {
        super("ResetPassword.InvalidResetToken", "The process of reset token has been failed.");
    }
}
