package onon1101.lendingsystem.auth.emailVerificationResend;

import java.util.Optional;
import java.util.UUID;
import onon1101.lendingsystem.auth.commons.UserStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcEmailVerificationAccountReader implements EmailVerificationAccountReader {

    private final JdbcClient jdbcClient;

    public JdbcEmailVerificationAccountReader(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<EmailVerificationAccount> findPendingByEmail(String email) {
        return jdbcClient
                .sql(
                        """
                        SELECT public_id, username, email
                        FROM users
                        WHERE email = :email
                            AND email_verified = FALSE
                            AND status = :active
                        LIMIT 1;
                        """)
                .param("email", email)
                .param("active", UserStatus.ACTIVE.value())
                .query(
                        (resultSet, rowNumber) ->
                                new EmailVerificationAccount(
                                        resultSet.getObject("public_id", UUID.class),
                                        resultSet.getString("username"),
                                        resultSet.getString("email")))
                .optional();
    }
}
