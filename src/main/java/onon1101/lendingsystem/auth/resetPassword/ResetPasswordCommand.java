package onon1101.lendingsystem.auth.resetPassword;

import org.jspecify.annotations.NonNull;

public record ResetPasswordCommand(
        String resetToken,
        String newPassword
) {

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
