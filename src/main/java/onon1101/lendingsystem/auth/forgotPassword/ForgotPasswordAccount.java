package onon1101.lendingsystem.auth.forgotPassword;

import java.util.UUID;

public record ForgotPasswordAccount(
        UUID publicUserId,
        String username
) {
}
