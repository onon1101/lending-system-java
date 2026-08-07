package onon1101.lendingsystem.user.validateEmail;

import onon1101.lendingsystem.sharedkernel.Command;

public record ValidateEmailCommand(String validateToken) implements Command {}
