package onon1101.lendingsystem.auth.login;

import java.util.Optional;
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
                    id,
                    username,
                    password_hash
                FROM users
                WHERE username = :username
                LIMIT 1
                """;

        return jdbcClient
                .sql(sql)
                .param("username", username)
                .query(
                        (resultSet, rowNumber) ->
                                new LoginAccount(
                                        resultSet.getLong("id"),
                                        resultSet.getString("username"),
                                        resultSet.getString("password_hash")))
                .optional();
    }
}
