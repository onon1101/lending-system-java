package onon1101.lendingsystem.user.register;

import java.util.Optional;

public interface RegisterAccountWriter {
    Optional<RegisterAccount> registerAccount(String username, String password, String email);
}
