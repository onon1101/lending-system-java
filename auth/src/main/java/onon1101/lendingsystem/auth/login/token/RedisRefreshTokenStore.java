package onon1101.lendingsystem.auth.login.token;

import java.time.Duration;
import onon1101.lendingsystem.configurations.redis.RedisKeyFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Repository
public class RedisRefreshTokenStore implements RefreshTokenStore {

    private static final String MODULE = "auth";
    private static final String RESOURCE = "refresh-token";

    private final ReactiveStringRedisTemplate redisTemplate;
    private final RedisKeyFactory keyFactory;
    private final ObjectMapper objectMapper;

    public RedisRefreshTokenStore(
            ReactiveStringRedisTemplate redisTemplate,
            RedisKeyFactory keyFactory,
            ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.keyFactory = keyFactory;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> save(String tokenHash, RefreshTokenSession session, Duration expiration) {
        String key = keyFactory.create(MODULE, RESOURCE, tokenHash);

        try {
            String value = objectMapper.writeValueAsString(session);
            return redisTemplate
                    .opsForValue()
                    .set(key, value, expiration)
                    .then()
                    .onErrorMap(
                            DataAccessException.class,
                            exception ->
                                    new IllegalStateException(
                                            "Unable to persist refresh token", exception));
        } catch (JacksonException exception) {
            return Mono.error(
                    new IllegalStateException(
                            "Unable to serialize refresh-token session", exception));
        }
    }

    @Override
    public Mono<RefreshTokenSession> find(String tokenHash) {
        String key = keyFactory.create(MODULE, RESOURCE, tokenHash);

        return redisTemplate
                .opsForValue()
                .get(key)
                .flatMap(value -> deserialize(key, value))
                .onErrorMap(
                        DataAccessException.class,
                        exception ->
                                new IllegalStateException(
                                        "Unable to read refresh token", exception));
    }

    @Override
    public Mono<Void> delete(String tokenHash) {
        return redisTemplate
                .delete(keyFactory.create(MODULE, RESOURCE, tokenHash))
                .then()
                .onErrorMap(
                        DataAccessException.class,
                        exception ->
                                new IllegalStateException(
                                        "Unable to delete refresh token.", exception));
    }

    @Override
    public Mono<RefreshTokenSession> consume(String tokenHash) {
        String key = keyFactory.create(MODULE, RESOURCE, tokenHash);

        return redisTemplate
                .opsForValue()
                .getAndDelete(key)
                .flatMap(value -> deserialize(key, value))
                .onErrorMap(
                        DataAccessException.class,
                        exception ->
                                new IllegalStateException(
                                        "Unable to consume refresh token", exception));
    }

    private Mono<RefreshTokenSession> deserialize(String key, String value) {
        try {
            return Mono.just(objectMapper.readValue(value, RefreshTokenSession.class));
        } catch (JacksonException exception) {
            return redisTemplate.delete(key).then(Mono.empty());
        }
    }
}
