package onon1101.lendingsystem.auth.emailVerificationConfirm;

import java.util.UUID;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcValidateEmailWriter implements ValidateEmailWriter {

    private final DatabaseClient databaseClient;

    R2dbcValidateEmailWriter(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Boolean> updateStateByPublicId(UUID publicId) {
        String sql =
                """
                UPDATE users
                SET email_verified = TRUE,
                    updated_at = CURRENT_TIMESTAMP
                WHERE public_id = :userPublicId
                  AND email_verified IS DISTINCT FROM TRUE;
                """;

        return databaseClient
                .sql(sql)
                .bind("userPublicId", publicId)
                .fetch()
                .rowsUpdated()
                .map(rows -> rows == 1);
    }
}
