package onon1101.lendingsystem.configurations.email;

public interface EmailVerificationMailService {

    void send(String recipientEmail, String username, String token);
}
