package onon1101.lendingsystem.auth.resetPassword;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Repository
final class JdbcResetPasswordWriter implements ResetPasswordWriter {

    private final JdbcClient jdbcClient;

    JdbcResetPasswordWriter(
            JdbcClient jdbcClient
    ) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public boolean updatePassword(
            UUID publicUserId,
            String encodedPassword,
            Instant tokenIssuedAt) {

        String sql = """
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

        return jdbcClient
                .sql(sql)
                .param("passwordHash", encodedPassword)
                .param("publicUserId", publicUserId)
                .param("tokenIssuedAt", Timestamp.from(tokenIssuedAt))
                .update() == 1;
    }
}
