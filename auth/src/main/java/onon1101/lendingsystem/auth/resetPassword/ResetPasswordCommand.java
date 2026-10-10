package onon1101.lendingsystem.auth.resetPassword;

import onon1101.lendingsystem.shared.command.Command;
import org.jspecify.annotations.NonNull;

public record ResetPasswordCommand(String resetToken, String newPassword) implements Command {

    @Override
    public @NonNull String toString() {
        return """
                ResetPasswordCommand[
                    resetToken=REDACTED,
                    newPassword=REDACTED
                ]
                """;
    }
}
