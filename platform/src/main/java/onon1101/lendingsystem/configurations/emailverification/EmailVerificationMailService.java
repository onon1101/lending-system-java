package onon1101.lendingsystem.configurations.emailverification;

public interface EmailVerificationMailService {

    void send(String recipientEmail, String username, String token);
}
