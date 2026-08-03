package onon1101.lendingsystem.auth.login;

import onon1101.lendingsystem.sharedkernel.ICommand;

import java.util.Locale;

public record LoginCommand(String username, String password) implements ICommand {

    @Override
    public String username() {
        return username.trim().toLowerCase(Locale.ROOT);
    }
}
