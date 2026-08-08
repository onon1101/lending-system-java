package onon1101.lendingsystem.auth.logout;


import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
        @NotBlank(message = "Refresh token must not be blank.")
        String refreshToken
) {
}
