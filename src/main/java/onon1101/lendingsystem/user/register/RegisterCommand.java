package onon1101.lendingsystem.user.register;

import java.util.Locale;
import onon1101.lendingsystem.sharedkernel.ICommand;

public record RegisterCommand(String username, String password, String email) implements ICommand {

    @Override
    public String username() {
        return username.trim().toLowerCase(Locale.ROOT);
    }

    @Override
    public String email() {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
