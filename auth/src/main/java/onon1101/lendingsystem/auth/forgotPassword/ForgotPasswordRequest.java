package onon1101.lendingsystem.auth.forgotPassword;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ForgotPasswordRequest(@NotBlank @Size(max = 255) String email) {}
