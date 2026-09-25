package onon1101.lendingsystem.auth.login;

import java.time.OffsetDateTime;
import java.util.UUID;
import onon1101.lendingsystem.auth.commons.IdentityProvider;
import onon1101.lendingsystem.auth.commons.UserStatus;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcLoginAccountReader implements LoginAccountReader {

    private final DatabaseClient databaseClient;

    public R2dbcLoginAccountReader(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<LoginAccount> findByUsername(String username) {
        String sql =
                """
                        SELECT
                            users.id AS private_user_id,
                            users.public_id,
                            users.username,
                            users.email,
                            credentials.password_hash,
                            credentials.auth_identity_id,
                            credentials.locked_until
                        FROM users
                        JOIN user_auth_identities identities
                            ON identities.user_id = users.id
                            AND identities.provider = :provider
                        JOIN user_password_credentials credentials
                            ON credentials.auth_identity_id = identities.id
                        WHERE users.username = :username
                            AND users.status = :active
                        LIMIT 1
                        """;

        return databaseClient
                .sql(sql)
                .bind("username", username)
                .bind("provider", IdentityProvider.PASSWORD.value())
                .bind("active", UserStatus.ACTIVE.value())
                .map(
                        (row, metadata) -> {
                            OffsetDateTime lockedUntil =
                                    row.get("locked_until", OffsetDateTime.class);

                            return new LoginAccount(
                                    require(row.get("private_user_id", Long.class)),
                                    require(row.get("public_id", UUID.class)),
                                    require(row.get("username", String.class)),
                                    require(row.get("password_hash", String.class)),
                                    require(row.get("email", String.class)),
                                    require(row.get("auth_identity_id", Long.class)),
                                    lockedUntil == null ? null : lockedUntil.toInstant());
                        })
                .one();
    }

    private static <T> T require(T value) {
        if (value == null) throw new IllegalStateException("Required login column was null");
        return value;
    }
}
