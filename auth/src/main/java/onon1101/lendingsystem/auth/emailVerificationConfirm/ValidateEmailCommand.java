package onon1101.lendingsystem.auth.emailVerificationConfirm;

import onon1101.lendingsystem.shared.command.Command;

public record ValidateEmailCommand(String validateToken) implements Command {}
