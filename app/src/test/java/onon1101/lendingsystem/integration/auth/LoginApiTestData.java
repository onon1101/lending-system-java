package onon1101.lendingsystem.integration.auth;

import com.github.f4b6a3.uuid.UuidCreator;
import onon1101.lendingsystem.integration.support.ApiTestData;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public final class LoginApiTestData implements ApiTestData {

    private final DatabaseClient databaseClient;
    private final PasswordEncoder passwordEncoder;

    public LoginApiTestData(DatabaseClient databaseClient, PasswordEncoder passwordEncoder) {
        this.databaseClient = databaseClient;
        this.passwordEncoder = passwordEncoder;
    }

    public void activePasswordUser(String username, String rawPassword) {
        Long userId =
                databaseClient
                        .sql(
                                """
                        INSERT INTO users
                            (username, public_id, status, email, email_verified)
                        VALUES
                                (:username, :publicId, 'active', :email, FALSE)
                        """)
                        .bind("username", username)
                        .bind("publicId", UuidCreator.getTimeOrderedEpoch())
                        .bind("email", username + "@example.com")
                        .filter(statement -> statement.returnGeneratedValues("id"))
                        .map((row, metadata) -> row.get("id", Long.class))
                        .one()
                        .block();

        Long identityId =
                databaseClient
                        .sql(
                                """
                        INSERT INTO user_auth_identities
                            (user_id, provider, provider_subject)
                        VALUES
                            (:userId, 'password', :username)
                        """)
                        .bind("userId", userId)
                        .bind("username", username)
                        .filter(statement -> statement.returnGeneratedValues("id"))
                        .map((row, metadata) -> row.get("id", Long.class))
                        .one()
                        .block();

        databaseClient
                .sql(
                        """
                        INSERT INTO user_password_credentials (auth_identity_id, password_hash)
                        VALUES (:identityId, :passwordHash)
                        """)
                .bind("identityId", identityId)
                .bind("passwordHash", passwordEncoder.encode(rawPassword))
                .fetch()
                .rowsUpdated()
                .block();

        //        return userId;
    }
}
