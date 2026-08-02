package onon1101.lendingsystem.auth.forgotPassword;

import java.util.Optional;
import java.util.UUID;
import onon1101.lendingsystem.auth.properties.IdentityProvider;
import onon1101.lendingsystem.auth.properties.UserStatus;
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
                	a.public_id,
                	a.username
                FROM user_auth_identities a
                LEFT JOIN users b ON a.user_id = b.id
                WHERE a.provider = :provider
                	AND email = :email
                	AND email_verified = true
                	AND b.status = :active
                LIMIT 1;
                """;

        return jdbcClient
                .sql(sql)
                .param("email", email)
                .param("provider", IdentityProvider.PASSWORD.value())
                .param("active", UserStatus.ACTIVE.value())
                .query(
                        (resultSet, rowNumber) ->
                                new ForgotPasswordAccount(
                                        resultSet.getObject("public_id", UUID.class),
                                        resultSet.getString("username")))
                .optional();
    }
}
