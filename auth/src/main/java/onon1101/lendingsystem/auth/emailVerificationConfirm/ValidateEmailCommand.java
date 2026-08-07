package onon1101.lendingsystem.auth.emailVerificationConfirm;

import onon1101.lendingsystem.configurations.services.Command;

public record ValidateEmailCommand(String validateToken) implements Command {}
