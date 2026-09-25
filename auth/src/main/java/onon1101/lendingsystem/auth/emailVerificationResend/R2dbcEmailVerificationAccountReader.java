package onon1101.lendingsystem.auth.emailVerificationResend;

import java.util.UUID;
import onon1101.lendingsystem.auth.commons.UserStatus;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcEmailVerificationAccountReader implements EmailVerificationAccountReader {

    private final DatabaseClient databaseClient;

    public R2dbcEmailVerificationAccountReader(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<EmailVerificationAccount> findPendingByEmail(String email) {
        return databaseClient
                .sql(
                        """
                        SELECT public_id, username, email
                        FROM users
                        WHERE email = :email
                            AND email_verified = FALSE
                            AND status = :active
                        LIMIT 1;
                        """)
                .bind("email", email)
                .bind("active", UserStatus.ACTIVE.value())
                .map(
                        (row, metadata) ->
                                new EmailVerificationAccount(
                                        require(row.get("public_id", UUID.class)),
                                        require(row.get("username", String.class)),
                                        require(row.get("email", String.class))))
                .one();
    }

    private static <T> T require(T value) {
        if (value == null) throw new IllegalStateException("Required account column was null");
        return value;
    }
}
