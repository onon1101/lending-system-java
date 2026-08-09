package onon1101.lendingsystem.auth.emailVerificationResend;

import onon1101.lendingsystem.configurations.email.EmailNormalizer;
import onon1101.lendingsystem.configurations.services.Command;

public record ResendEmailVerificationCommand(String email) implements Command {

    public ResendEmailVerificationCommand {
        email = EmailNormalizer.normalize(email);
    }
}
