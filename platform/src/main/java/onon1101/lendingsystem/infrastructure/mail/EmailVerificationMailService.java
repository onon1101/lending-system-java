package onon1101.lendingsystem.infrastructure.mail;

public interface EmailVerificationMailService {

    void send(String recipientEmail, String username, String token);
}
