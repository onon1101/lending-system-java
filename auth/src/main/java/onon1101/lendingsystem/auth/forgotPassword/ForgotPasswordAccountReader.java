package onon1101.lendingsystem.auth.forgotPassword;

import java.util.Optional;

public interface ForgotPasswordAccountReader {
    Optional<ForgotPasswordAccount> findByEmail(String email);
}
