package onon1101.lendingsystem.user.validateEmail;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class JdbcValidateEmailWriter implements ValidateEmailWriter {

    private final JdbcClient jdbcClient;

    JdbcValidateEmailWriter(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public boolean updateStateByPublicId(
            UUID publicId
    ) {
        String sql = """
                UPDATE user_auth_identities AS identity
                SET email_verified = TRUE
                FROM users AS user_account
                WHERE identity.user_id = user_account.id
                  AND user_account.public_id = :userPublicId
                  AND identity.provider = 'password'
                  AND identity.email_verified IS DISTINCT FROM TRUE;
                """;

        return jdbcClient
                .sql(sql)
                .param("userPublicId", publicId)
                .update() == 1;
    }
}
