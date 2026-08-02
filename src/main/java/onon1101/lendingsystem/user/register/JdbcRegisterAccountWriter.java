package onon1101.lendingsystem.user.register;

import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcRegisterAccountWriter implements RegisterAccountWriter {

    private final JdbcClient jdbcClient;

    public JdbcRegisterAccountWriter(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<RegisterAccount> registerAccount(
            String username, String passwordHash, String email) {
        try {
            KeyHolder userKey = new GeneratedKeyHolder();
            jdbcClient
                    .sql("INSERT INTO users (username) VALUES (:username)")
                    .param("username", username)
                    .update(userKey, "id");
            long userId = userKey.getKeyAs(Long.class);

            KeyHolder identityKey = new GeneratedKeyHolder();
            jdbcClient
                    .sql(
                            """
                            INSERT INTO user_auth_identities
                                (user_id, provider, provider_subject, email, email_verified)
                            VALUES
                                (:userId, :provider, :subject, :email, FALSE)
                            """)
                    .param("userId", userId)
                    .param("provider", "password")
                    .param("subject", username)
                    .param("email", email)
                    .update(identityKey, "id");
            long identityId = identityKey.getKeyAs(Long.class);

            jdbcClient
                    .sql(
                            """
                            INSERT INTO user_password_credentials
                                (auth_identity_id, password_hash)
                            VALUES
                                (:identityId, :passwordHash)
                            """)
                    .param("identityId", identityId)
                    .param("passwordHash", passwordHash)
                    .update();

            return Optional.of(new RegisterAccount(userId));
        } catch (DataIntegrityViolationException exception) {
            return Optional.empty();
        }
    }
}
