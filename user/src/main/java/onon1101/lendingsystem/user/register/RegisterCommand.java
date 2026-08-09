package onon1101.lendingsystem.user.register;

import java.util.Locale;
import onon1101.lendingsystem.configurations.email.EmailNormalizer;
import onon1101.lendingsystem.configurations.services.Command;

public record RegisterCommand(String username, String password, String email) implements Command {

    @Override
    public String username() {
        return username.trim().toLowerCase(Locale.ROOT);
    }

    public RegisterCommand {
        email = EmailNormalizer.normalize(email);
    }
}
