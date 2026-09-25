package onon1101.lendingsystem.auth.forgotPassword;

import java.util.UUID;
import onon1101.lendingsystem.auth.commons.UserStatus;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcForgotPasswordAccountReader implements ForgotPasswordAccountReader {

    private final DatabaseClient databaseClient;

    public R2dbcForgotPasswordAccountReader(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<ForgotPasswordAccount> findByEmail(String email) {
        String sql =
                """
                SELECT
                    users.public_id,
                    users.username
                FROM users
                WHERE users.email = :email
                    AND users.email_verified = TRUE
                    AND users.status = :active
                LIMIT 1;
                """;

        return databaseClient
                .sql(sql)
                .bind("email", email)
                .bind("active", UserStatus.ACTIVE.value())
                .map(
                        (row, metadata) ->
                                new ForgotPasswordAccount(
                                        require(row.get("public_id", UUID.class)),
                                        require(row.get("username", String.class))))
                .one();
    }

    private static <T> T require(T value) {
        if (value == null) throw new IllegalStateException("Required account column was null");
        return value;
    }
}
