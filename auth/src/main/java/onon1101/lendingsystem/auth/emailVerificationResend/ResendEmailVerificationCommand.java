package onon1101.lendingsystem.auth.emailVerificationResend;

import onon1101.lendingsystem.configurations.services.Command;

import java.util.Locale;

public record ResendEmailVerificationCommand(String email) implements Command {

    public ResendEmailVerificationCommand {
        email = email.trim().toLowerCase(Locale.ROOT);
    }
}
