package onon1101.lendingsystem.user.register.email;

public interface EmailValidateMailService {
    void sendEmailValidateEmail(String recipientEmail, String username, String validateEmailToken);
}
