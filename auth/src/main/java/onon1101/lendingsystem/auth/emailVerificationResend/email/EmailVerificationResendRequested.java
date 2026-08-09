package onon1101.lendingsystem.auth.emailVerificationResend.email;

public record EmailVerificationResendRequested(String email, String username, String token) {}
