package onon1101.lendingsystem.sharedkernel.context.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcCurrentUserReader implements CurrentUserReader {

    private final JdbcClient jdbcClient;

    public JdbcCurrentUserReader(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<CurrentUserContext> findByPrivateId(long privateUserId) {
        String sql =
                """
                SELECT
                    users.id AS private_user_id,
                    users.public_id,
                    users.username,
                    users.status,
                    users.email
                FROM users
                WHERE users.id = :privateUserId
                LIMIT 1
                """;

        return jdbcClient
                .sql(sql)
                .param("privateUserId", privateUserId)
                .query(
                        (resultSet, rowNumber) ->
                                new CurrentUserContext(
                                        resultSet.getLong("private_user_id"),
                                        resultSet.getObject("public_id", UUID.class),
                                        resultSet.getString("username"),
                                        resultSet.getString("email"),
                                        resultSet.getString("status")))
                .optional();
    }
}
