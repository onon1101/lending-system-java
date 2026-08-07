package onon1101.lendingsystem.auth.forgotPassword;

import java.util.Locale;
import onon1101.lendingsystem.sharedkernel.Command;

public record ForgotPasswordCommand(String email) implements Command {

    @Override
    public String email() {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
