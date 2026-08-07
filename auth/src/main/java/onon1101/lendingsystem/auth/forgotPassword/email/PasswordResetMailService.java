package onon1101.lendingsystem.auth.forgotPassword.email;

public interface PasswordResetMailService {
    void sendResetPasswordEmail(String recipientEmail, String username, String resetToken);
}
