package onon1101.lendingsystem.auth.login;

import java.util.Optional;

public interface LoginAccountReader {
    Optional<LoginAccount> findByUsername(String username);
}
