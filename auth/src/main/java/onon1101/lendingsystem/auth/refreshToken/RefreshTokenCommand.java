package onon1101.lendingsystem.auth.refreshToken;

import onon1101.lendingsystem.configurations.services.Command;

public record RefreshTokenCommand(
        String refreshToken
) implements Command {
}
