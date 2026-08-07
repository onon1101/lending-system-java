package onon1101.lendingsystem.auth.login;

import java.util.Locale;
import onon1101.lendingsystem.sharedkernel.Command;

public record LoginCommand(String username, String password) implements Command {

    @Override
    public String username() {
        return username.trim().toLowerCase(Locale.ROOT);
    }
}
