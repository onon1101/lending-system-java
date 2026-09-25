package onon1101.lendingsystem.auth.resetPassword;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
class R2dbcResetPasswordWriter implements ResetPasswordWriter {

    private final DatabaseClient databaseClient;

    R2dbcResetPasswordWriter(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Boolean> updatePassword(
            UUID publicUserId, String encodedPassword, Instant tokenIssuedAt) {

        String sql =
                """
                UPDATE user_password_credentials
                SET password_hash = :passwordHash,
                	password_changed_at = CURRENT_TIMESTAMP,
                	failed_attempts = 0,
                	locked_until = NULL
                WHERE auth_identity_id = (
                	SELECT
                		a.id
                	FROM user_auth_identities a
                	LEFT JOIN users b ON a.user_id = b.id
                	WHERE b.public_id = :publicUserId
                		AND a.provider = 'password'
                )
                AND password_changed_at <= :tokenIssuedAt
                """;

        return databaseClient
                .sql(sql)
                .bind("passwordHash", encodedPassword)
                .bind("publicUserId", publicUserId)
                .bind("tokenIssuedAt", tokenIssuedAt.atOffset(ZoneOffset.UTC))
                .fetch()
                .rowsUpdated()
                .map(rows -> rows == 1);
    }
}
