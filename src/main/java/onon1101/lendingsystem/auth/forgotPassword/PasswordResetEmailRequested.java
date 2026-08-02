package onon1101.lendingsystem.auth.forgotPassword;

/** Event emitted when a password-reset email should be delivered. */
public record PasswordResetEmailRequested(String email, String username, String resetToken) {}
