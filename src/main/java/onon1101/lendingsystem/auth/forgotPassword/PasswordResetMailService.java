package onon1101.lendingsystem.auth.forgotPassword;

public interface PasswordResetMailService {
    void sendResetPasswordEmail(String recipientEmail, String username, String resetToken);
}
