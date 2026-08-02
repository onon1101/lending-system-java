package onon1101.lendingsystem.user.register;

import com.github.f4b6a3.uuid.UuidCreator;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
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
            UUID publicId = UuidCreator.getTimeOrderedEpoch();

            KeyHolder userKey = new GeneratedKeyHolder();
            jdbcClient
                    .sql(
                            """
                            INSERT INTO users (username, public_id)
                            VALUES (:username, :publicId)
                            """)
                    .param("username", username)
                    .param("publicId", publicId)
                    .update(userKey, "id");

            long userId =
                    Objects.requireNonNull(
                            userKey.getKeyAs(Long.class),
                            "Database did not return the generated user.id");

            RegisterAccount account = new RegisterAccount(userId, publicId);

            KeyHolder identityKey = new GeneratedKeyHolder();
            jdbcClient
                    .sql(
                            """
                                    INSERT INTO user_auth_identities
                                        (user_id, provider, provider_subject, email, email_verified)
                                    VALUES
                                        (:userId, :provider, :subject, :email, FALSE)
                                    """)
                    .param("userId", account.privateUserId())
                    .param("provider", "password")
                    .param("subject", username)
                    .param("email", email)
                    .update(identityKey, "id");

            long identityId =
                    Objects.requireNonNull(
                            identityKey.getKeyAs(Long.class),
                            "Database did not return the generated identity.id");

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

            return Optional.of(account);
        } catch (DataIntegrityViolationException exception) {
            return Optional.empty();
        }
    }
}
