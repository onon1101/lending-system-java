package onon1101.lendingsystem.configurations.context.user;

import java.util.UUID;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcCurrentUserReader implements CurrentUserReader {

    private final DatabaseClient databaseClient;

    public R2dbcCurrentUserReader(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<CurrentUserContext> findByPrivateId(long privateUserId) {
        String sql =
                """
                SELECT
                    users.id AS private_user_id,
                    users.public_id,
                    users.username,
                    users.status,
                    users.email
                FROM users
                WHERE users.id = :privateUserId
                LIMIT 1
                """;

        return databaseClient
                .sql(sql)
                .bind("privateUserId", privateUserId)
                .map(
                        (row, metadata) ->
                                new CurrentUserContext(
                                        require(row.get("private_user_id", Long.class)),
                                        require(row.get("public_id", UUID.class)),
                                        require(row.get("username", String.class)),
                                        require(row.get("email", String.class)),
                                        require(row.get("status", String.class))))
                .one();
    }

    private static <T> T require(T value) {
        if (value == null) {
            throw new IllegalStateException("Required current-user column was null");
        }
        return value;
    }
}
