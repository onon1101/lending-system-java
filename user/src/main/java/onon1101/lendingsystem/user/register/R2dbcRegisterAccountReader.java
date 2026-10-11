package onon1101.lendingsystem.user.register;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcRegisterAccountReader implements RegisterAccountReader {

    private final DatabaseClient databaseClient;

    public R2dbcRegisterAccountReader(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Boolean> existsByUsernameOrEmail(String username, String email) {
        return databaseClient
                .sql(
                        """
                     SELECT EXISTS (
                        SELECT 1
                        FROM users
                        WHERE username = :username
                            OR email = :email
                     ) AS account_exists
                     """)
                .bind("username", username)
                .bind("email", email)
                .map(
                        (row, metadata) -> {
                            Boolean exists = row.get("account_exists", Boolean.class);

                            if (exists == null) {
                                throw new IllegalStateException(
                                        "Account existence result was null.");
                            }

                            return exists;
                        })
                .one();
    }
}
