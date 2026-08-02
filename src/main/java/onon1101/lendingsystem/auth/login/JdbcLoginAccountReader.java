package onon1101.lendingsystem.auth.login;

import java.util.Optional;
import onon1101.lendingsystem.auth.properties.IdentityProvider;
import onon1101.lendingsystem.auth.properties.UserStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcLoginAccountReader implements LoginAccountReader {

    private final JdbcClient jdbcClient;

    public JdbcLoginAccountReader(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<LoginAccount> findByUsername(String username) {
        String sql =
                """
                        SELECT
                            users.id,
                            users.username,
                            credentials.password_hash
                        FROM users
                        JOIN user_auth_identities identities
                            ON identities.user_id = users.id
                            AND identities.provider = :provider
                        JOIN user_password_credentials credentials
                            ON credentials.auth_identity_id = identities.id
                        WHERE users.username = :username
                            AND users.status = :active
                        LIMIT 1
                        """;

        return jdbcClient
                .sql(sql)
                .param("username", username)
                .param("provider", IdentityProvider.PASSWORD.value())
                .param("active", UserStatus.ACTIVE.value())
                .query(
                        (resultSet, rowNumber) ->
                                new LoginAccount(
                                        resultSet.getLong("id"),
                                        resultSet.getString("username"),
                                        resultSet.getString("password_hash")))
                .optional();
    }
}
