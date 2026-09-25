package onon1101.lendingsystem.auth.login;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcLoginAccountWriter implements LoginAccountWriter {

    private final DatabaseClient databaseClient;

    public R2dbcLoginAccountWriter(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<FailedAttemptResult> recordFailedAttempt(
            Long passwordId, int maxAttempts, Instant lockedUntil) {

        String sql =
                """
                        UPDATE user_password_credentials
                        SET failed_attempts = CASE
                                WHEN locked_until IS NOT NULL
                                    AND locked_until <= CURRENT_TIMESTAMP
                                THEN 1
                                ELSE failed_attempts + 1
                            END,
                            locked_until = CASE
                                WHEN (CASE
                                    WHEN locked_until IS NOT NULL
                                        AND locked_until <= CURRENT_TIMESTAMP
                                    THEN 1
                                    ELSE failed_attempts + 1
                                END) >= :maxAttempts
                                THEN :newLockedUntil
                                ELSE NULL
                            END
                        WHERE auth_identity_id = :passwordId
                        """;

        OffsetDateTime newLockedUntil = lockedUntil.atOffset(ZoneOffset.UTC);

        return databaseClient
                .sql(sql)
                .bind("passwordId", passwordId)
                .bind("maxAttempts", maxAttempts)
                .bind("newLockedUntil", newLockedUntil)
                .fetch()
                .rowsUpdated()
                .flatMap(
                        rows ->
                                rows == 1
                                        ? readFailedAttempt(passwordId)
                                        : Mono.error(
                                                new IllegalStateException(
                                                        "Failed to record login attempt, passwordId="
                                                                + passwordId)));
    }

    private Mono<FailedAttemptResult> readFailedAttempt(Long passwordId) {
        return databaseClient
                .sql(
                        """
                        SELECT failed_attempts, locked_until
                        FROM user_password_credentials
                        WHERE auth_identity_id = :passwordId
                        """)
                .bind("passwordId", passwordId)
                .map(
                        (row, metadata) -> {
                            OffsetDateTime resultLockedUntil =
                                    row.get("locked_until", OffsetDateTime.class);
                            return new FailedAttemptResult(
                                    require(row.get("failed_attempts", Integer.class)),
                                    resultLockedUntil == null
                                            ? null
                                            : resultLockedUntil.toInstant());
                        })
                .one();
    }

    @Override
    public Mono<Void> resetFailedAttempts(Long passwordId) {
        String sql =
                """
                        UPDATE user_password_credentials
                        SET failed_attempts = 0,
                            locked_until = NULL
                        WHERE auth_identity_id = :passwordId
                        """;

        return databaseClient
                .sql(sql)
                .bind("passwordId", passwordId)
                .fetch()
                .rowsUpdated()
                .flatMap(
                        rows ->
                                rows == 1
                                        ? Mono.<Void>empty()
                                        : Mono.error(
                                                new IllegalStateException(
                                                        "Reset failed attempts failed, passwordId="
                                                                + passwordId)));
    }

    private static <T> T require(T value) {
        if (value == null) throw new IllegalStateException("Required login column was null");
        return value;
    }
}
