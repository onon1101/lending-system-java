package onon1101.lendingsystem.auth.login.token;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;
import onon1101.lendingsystem.configurations.time.IClock;

@Service
public class RefreshTokenService {

    private static final int TOKEN_BYTES = 32;

    private final SecureRandom secureRandom = new SecureRandom();
    private final RefreshTokenStore tokenStore;
    private final RefreshTokenProperties properties;
    private final IClock clock;

    public RefreshTokenService(
            RefreshTokenStore tokenStore, RefreshTokenProperties properties, IClock clock) {
        this.tokenStore = tokenStore;
        this.properties = properties;
        this.clock = clock;
    }

    public String createToken(
            long privateUserId,
            UUID publicUserId,
            String username
    ) {
        byte[] tokenBytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(tokenBytes);

        String rawToken =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(tokenBytes);

        RefreshTokenSession session =
                new RefreshTokenSession(
                        privateUserId,
                        publicUserId,
                        username,
                        clock.now());

        tokenStore.save(
                hash(rawToken),
                session,
                properties.expiration());

        return rawToken;
    }

    public long expiresInSeconds() {
        return properties.expiration().toSeconds();
    }

    public String hash(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalStateException(
                    "Refresh token must not be blank."
            );
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashed =
                    digest.digest(
                            rawToken.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hashed);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 is not available.",
                    exception
            );
        }
    }

    public void revoke(
            String rawToken
    ) {
        tokenStore.delete(hash(rawToken));
    }
}
