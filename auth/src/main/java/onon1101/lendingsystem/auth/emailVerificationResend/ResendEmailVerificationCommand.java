package onon1101.lendingsystem.auth.emailVerificationResend;

import onon1101.lendingsystem.shared.email.EmailNormalizer;
import onon1101.lendingsystem.shared.command.Command;

public record ResendEmailVerificationCommand(String email) implements Command {

    public ResendEmailVerificationCommand {
        email = EmailNormalizer.normalize(email);
    }
}
