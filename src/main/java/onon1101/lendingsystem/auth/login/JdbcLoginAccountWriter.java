package onon1101.lendingsystem.auth.login;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcLoginAccountWriter implements LoginAccountWriter {

    private final JdbcClient jdbcClient;

    public JdbcLoginAccountWriter(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public FailedAttemptResult recordFailedAttempt(
            Integer passwordId,
            int maxAttempts,
            Instant lockedUntil
    ) {

        String sql =
                """
                        WITH attempt AS (
                        	SELECT
                        		auth_identity_id,
                        		CASE
                        			WHEN locked_until IS NOT NULL
                        				AND locked_until <= CURRENT_TIMESTAMP
                        			THEN 1
                        			ELSE failed_attempts + 1
                        		END AS next_failed_attempts
                        	FROM user_password_credentials
                        	WHERE auth_identity_id = :passwordId
                        )
                        UPDATE user_password_credentials c
                        SET
                        	failed_attempts = attempt.next_failed_attempts,
                        	locked_until = CASE
                        		WHEN attempt.next_failed_attempts >= :maxAttempts
                        		THEN :newLockedUntil
                        		ELSE NULL
                        	END
                        FROM attempt
                        WHERE c.auth_identity_id = attempt.auth_identity_id
                        RETURNING
                        	c.failed_attempts,
                        	c.locked_until
                        """;

        OffsetDateTime newLockedUntil = lockedUntil.atOffset(ZoneOffset.UTC);

        return jdbcClient
                .sql(sql)
                .param("passwordId", passwordId)
                .param("maxAttempts", maxAttempts)
                .param("newLockedUntil", newLockedUntil)
                .query(
                        (rs, rowNum) -> {
                            OffsetDateTime resultLockedUntil =
                                    rs.getObject("locked_until",
                                            OffsetDateTime.class);

                            return new FailedAttemptResult(
                                    rs.getInt("failed_attempts"),
                                    resultLockedUntil == null
                                            ? null
                                            : resultLockedUntil.toInstant());
                        })
                .single();
    }

    @Override
    public void resetFailedAttempts(Integer passwordId) {
        String sql =
                """
                        UPDATE user_password_credentials
                        SET failed_attempts = 0,
                            locked_until = NULL
                        WHERE auth_identity_id = :passwordId
                        """;

        int affectedRows = jdbcClient
                .sql(sql)
                .param("passwordId", passwordId)
                .update();

        if (affectedRows != 1) {
            throw new IllegalStateException(
                    "Reset failed attempts failed, passwordId=" + passwordId);
        }
    }
}
