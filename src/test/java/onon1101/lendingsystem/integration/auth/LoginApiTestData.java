package onon1101.lendingsystem.integration.auth;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import onon1101.lendingsystem.integration.support.ApiTestData;

@Component
public final class LoginApiTestData implements ApiTestData {

    private final JdbcClient jdbcClient;
    private final PasswordEncoder passwordEncoder;

    public LoginApiTestData(JdbcClient jdbcClient, PasswordEncoder passwordEncoder) {
        this.jdbcClient = jdbcClient;
        this.passwordEncoder = passwordEncoder;
    }

    public void activePasswordUser(String username, String rawPassword) {
        jdbcClient
                .sql("INSERT INTO users (username, status) VALUES (:username, 'active')")
                .param("username", username)
                .update();

        long userId = jdbcClient
                .sql("SELECT id FROM users WHERE username = :username")
                .param("username", username)
                .query(Long.class)
                .single();

        jdbcClient
                .sql("""
                        INSERT INTO user_auth_identities
                            (user_id, provider, provider_subject, email_verified)
                        VALUES
                            (:userId, 'password', :username, false)
                        """)
                .param("userId", userId)
                .param("username", username)
                .update();

        long identityId = jdbcClient
                .sql("""
                        SELECT id
                        FROM user_auth_identities
                        WHERE user_id = :userId AND provider = 'password'
                        """)
                .param("userId", userId)
                .query(Long.class)
                .single();

        jdbcClient
                .sql("""
                        INSERT INTO user_password_credentials (auth_identity_id, password_hash)
                        VALUES (:identityId, :passwordHash)
                        """)
                .param("identityId", identityId)
                .param("passwordHash", passwordEncoder.encode(rawPassword))
                .update();

//        return userId;
    }
}
