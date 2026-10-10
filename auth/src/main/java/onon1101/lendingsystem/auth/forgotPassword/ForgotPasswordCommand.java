package onon1101.lendingsystem.auth.forgotPassword;

import onon1101.lendingsystem.shared.email.EmailNormalizer;
import onon1101.lendingsystem.shared.command.Command;

public record ForgotPasswordCommand(String email) implements Command {

    public ForgotPasswordCommand {
        email = EmailNormalizer.normalize(email);
    }
}
