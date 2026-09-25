package onon1101.lendingsystem.user.register;

import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcRegisterAccountWriter implements RegisterAccountWriter {

    private final DatabaseClient databaseClient;

    public R2dbcRegisterAccountWriter(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<RegisterAccount> registerAccount(
            String username, String passwordHash, String email) {
        UUID publicId = UuidCreator.getTimeOrderedEpoch();
        return databaseClient
                .sql(
                        """
                        INSERT INTO users (username, public_id, email, email_verified)
                        VALUES (:username, :publicId, :email, FALSE)
                        """)
                .bind("username", username)
                .bind("publicId", publicId)
                .bind("email", email)
                .filter(statement -> statement.returnGeneratedValues("id"))
                .map((row, metadata) -> require(row.get("id", Long.class)))
                .one()
                .flatMap(
                        userId ->
                                createIdentity(userId, username)
                                        .flatMap(
                                                identityId ->
                                                        createPassword(identityId, passwordHash))
                                        .thenReturn(new RegisterAccount(userId, publicId)))
                .onErrorResume(DataIntegrityViolationException.class, ignored -> Mono.empty());
    }

    private Mono<Long> createIdentity(long userId, String username) {
        return databaseClient
                .sql(
                        """
                        INSERT INTO user_auth_identities
                            (user_id, provider, provider_subject)
                        VALUES (:userId, 'password', :subject)
                        """)
                .bind("userId", userId)
                .bind("subject", username)
                .filter(statement -> statement.returnGeneratedValues("id"))
                .map((row, metadata) -> require(row.get("id", Long.class)))
                .one();
    }

    private Mono<Void> createPassword(long identityId, String passwordHash) {
        return databaseClient
                .sql(
                        """
                        INSERT INTO user_password_credentials
                            (auth_identity_id, password_hash)
                        VALUES (:identityId, :passwordHash)
                        """)
                .bind("identityId", identityId)
                .bind("passwordHash", passwordHash)
                .fetch()
                .rowsUpdated()
                .flatMap(
                        rows ->
                                rows == 1
                                        ? Mono.<Void>empty()
                                        : Mono.error(
                                                new IllegalStateException(
                                                        "Failed to create password credential")));
    }

    private static <T> T require(T value) {
        if (value == null) throw new IllegalStateException("Generated id was null");
        return value;
    }
}
