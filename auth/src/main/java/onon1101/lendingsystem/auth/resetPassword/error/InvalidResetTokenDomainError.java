package onon1101.lendingsystem.auth.resetPassword.error;

import onon1101.lendingsystem.configurations.domain.DomainError;

public final class InvalidResetTokenDomainError extends DomainError {
    public InvalidResetTokenDomainError() {
        super("ResetPassword.InvalidResetToken", "The process of reset token has been failed.");
    }
}
