package onon1101.lendingsystem.auth.resetPassword;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank String resetToken,
        @NotBlank
        @Size(min = 12, max = 128)
        String newPassword
) {
}
