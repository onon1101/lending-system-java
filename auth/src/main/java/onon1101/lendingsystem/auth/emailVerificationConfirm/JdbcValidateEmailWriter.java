package onon1101.lendingsystem.auth.emailVerificationConfirm;

import java.util.UUID;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcValidateEmailWriter implements ValidateEmailWriter {

    private final JdbcClient jdbcClient;

    JdbcValidateEmailWriter(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public boolean updateStateByPublicId(UUID publicId) {
        String sql =
                """
                UPDATE users
                SET email_verified = TRUE,
                    updated_at = CURRENT_TIMESTAMP
                WHERE public_id = :userPublicId
                  AND email_verified IS DISTINCT FROM TRUE;
                """;

        return jdbcClient.sql(sql).param("userPublicId", publicId).update() == 1;
    }
}
