package onon1101.lendingsystem.user.validateEmail;

import onon1101.lendingsystem.sharedkernel.ICommand;

public record ValidateEmailCommand (
        String validateToken
) implements ICommand { }
