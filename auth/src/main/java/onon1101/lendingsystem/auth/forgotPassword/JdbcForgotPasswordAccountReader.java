package onon1101.lendingsystem.auth.forgotPassword;

import java.util.Optional;
import java.util.UUID;
import onon1101.lendingsystem.auth.commons.UserStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcForgotPasswordAccountReader implements ForgotPasswordAccountReader {

    private final JdbcClient jdbcClient;

    public JdbcForgotPasswordAccountReader(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<ForgotPasswordAccount> findByEmail(String email) {
        String sql =
                """
                SELECT
                    users.public_id,
                    users.username
                FROM users
                WHERE users.email = :email
                    AND users.email_verified = TRUE
                    AND users.status = :active
                LIMIT 1;
                """;

        return jdbcClient
                .sql(sql)
                .param("email", email)
                .param("active", UserStatus.ACTIVE.value())
                .query(
                        (resultSet, rowNumber) ->
                                new ForgotPasswordAccount(
                                        resultSet.getObject("public_id", UUID.class),
                                        resultSet.getString("username")))
                .optional();
    }
}
