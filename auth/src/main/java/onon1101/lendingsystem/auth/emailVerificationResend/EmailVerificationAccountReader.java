package onon1101.lendingsystem.auth.emailVerificationResend;

import java.util.Optional;

public interface EmailVerificationAccountReader {

    Optional<EmailVerificationAccount> findPendingByEmail(String email);
}
