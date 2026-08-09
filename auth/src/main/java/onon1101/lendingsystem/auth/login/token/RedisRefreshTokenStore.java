package onon1101.lendingsystem.auth.login.token;

import onon1101.lendingsystem.configurations.redis.RedisKeyFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Optional;

@Repository
public class RedisRefreshTokenStore
        implements RefreshTokenStore {

    private static final String MODULE = "auth";
    private static final String RESOURCE = "refresh-token";

    private final StringRedisTemplate redisTemplate;
    private final RedisKeyFactory keyFactory;
    private final ObjectMapper objectMapper;

    public RedisRefreshTokenStore(
            StringRedisTemplate redisTemplate,
            RedisKeyFactory keyFactory,
            ObjectMapper objectMapper
    ) {
        this.redisTemplate = redisTemplate;
        this.keyFactory = keyFactory;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(
            String tokenHash,
            RefreshTokenSession session,
            Duration expiration
    ) {
        String key = keyFactory.create(MODULE, RESOURCE, tokenHash);

        try {
            String value = objectMapper.writeValueAsString(session);
            redisTemplate
                    .opsForValue()
                    .set(key, value, expiration);
        } catch (JacksonException exception) {
            throw new IllegalStateException(
                    "Unable to serialize refresh-token session",
                    exception
            );
        } catch (DataAccessException exception) {
            throw new IllegalStateException(
                    "Unable to persist refresh token",
                    exception
            );
        }
    }

    @Override
    public Optional<RefreshTokenSession> find(
            String tokenHash
    ) {
        String key = keyFactory.create(MODULE, RESOURCE, tokenHash);

        try {
            String value = redisTemplate
                    .opsForValue()
                    .get(key);

            if (value == null) {
                return Optional.empty();
            }

            return Optional.of(
                    objectMapper.readValue(value, RefreshTokenSession.class));
        } catch (JacksonException exception) {
            redisTemplate.delete(key);
            return Optional.empty();
        } catch (DataAccessException exception) {
            throw new IllegalStateException(
                    "Unable to read refresh token",
                    exception
            );
        }
    }

    @Override
    public void delete(String tokenHash) {
        try {
            redisTemplate.delete(
                    keyFactory.create(MODULE, RESOURCE, tokenHash));
        } catch (DataAccessException exception) {
            throw new IllegalStateException(
                    "Unable to delete refresh token.",
                    exception
            );
        }
    }

    @Override
    public Optional<RefreshTokenSession> consume(
            String tokenHash
    ) {
        String key = keyFactory.create(
                MODULE,
                RESOURCE,
                tokenHash
        );

        try {
            String value = redisTemplate
                    .opsForValue()
                    .getAndDelete(key);

            if (value == null) {
                return Optional.empty();
            }

            return Optional.of(
                    objectMapper.readValue(
                            value,
                            RefreshTokenSession.class
                    )
            );
        } catch (JacksonException exception) {
            return Optional.empty();
        } catch( DataAccessException exception) {
            throw new IllegalStateException(
                    "Unable to consume refresh token",
                    exception
            );
        }
    }
}
