package onon1101.lendingsystem.auth.forgotPassword;

import onon1101.lendingsystem.configurations.email.EmailNormalizer;
import onon1101.lendingsystem.configurations.services.Command;

public record ForgotPasswordCommand(String email) implements Command {

    public ForgotPasswordCommand {
        email = EmailNormalizer.normalize(email);
    }
}
