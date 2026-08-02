package onon1101.lendingsystem.user.register;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(max = 50) String username,
        @NotBlank @Size(max = 100) String password,
        @NotBlank @Size(max = 255) String email) {}
