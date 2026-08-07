package onon1101.lendingsystem.auth.login;

import java.util.Locale;
import onon1101.lendingsystem.sharedkernel.ICommand;

public record LoginCommand(String username, String password) implements ICommand {

    @Override
    public String username() {
        return username.trim().toLowerCase(Locale.ROOT);
    }
}
