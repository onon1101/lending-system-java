package onon1101.lendingsystem.auth.login.token;

import java.time.Duration;
import java.util.Optional;

public interface RefreshTokenStore {

    void save(
            String tokenHash,
            RefreshTokenSession session,
            Duration expiration
    );

    Optional<RefreshTokenSession> find(String tokenHash);

    void delete(String tokenHash);

    Optional<RefreshTokenSession> consume(String tokenHash);
}
