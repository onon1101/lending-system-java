package onon1101.lendingsystem.configurations.redis;

import onon1101.lendingsystem.configurations.context.user.CurrentUserContext;
import onon1101.lendingsystem.configurations.context.user.UserCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Repository
public class RedisUserCache implements UserCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisUserCache.class);
    private static final String MODULE = "user";
    private static final String RESOURCE = "context";

    private final ReactiveStringRedisTemplate redisTemplate;
    private final RedisKeyFactory keyFactory;
    private final ObjectMapper objectMapper;
    private final UserCacheProperties properties;

    public RedisUserCache(
            ReactiveStringRedisTemplate redisTemplate,
            RedisKeyFactory keyFactory,
            ObjectMapper objectMapper,
            UserCacheProperties properties) {
        this.redisTemplate = redisTemplate;
        this.keyFactory = keyFactory;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    @Override
    public Mono<CurrentUserContext> find(long privateUserId) {
        String key = key(privateUserId);

        return redisTemplate
                .opsForValue()
                .get(key)
                .flatMap(json -> deserialize(key, privateUserId, json))
                .onErrorResume(
                        DataAccessException.class,
                        exception -> {
                            LOGGER.warn("Unable to read current-user cache key={}", key, exception);
                            return Mono.empty();
                        });
    }

    @Override
    public Mono<Void> save(CurrentUserContext user) {
        String key = key(user.privateUserId());

        try {
            String json = objectMapper.writeValueAsString(user);
            return redisTemplate
                    .opsForValue()
                    .set(key, json, properties.ttl())
                    .then()
                    .onErrorResume(
                            DataAccessException.class,
                            exception -> {
                                LOGGER.warn(
                                        "Unable to write current-user cache key={}",
                                        key,
                                        exception);
                                return Mono.empty();
                            });
        } catch (JacksonException exception) {
            return Mono.error(
                    new IllegalStateException("Unable to serialize current-user cache", exception));
        }
    }

    @Override
    public Mono<Void> evict(long privateUserId) {
        String key = key(privateUserId);

        return redisTemplate
                .delete(key)
                .then()
                .onErrorResume(
                        DataAccessException.class,
                        exception -> {
                            LOGGER.warn(
                                    "Unable to evict current-user cache key={}", key, exception);
                            return Mono.empty();
                        });
    }

    private Mono<CurrentUserContext> deserialize(String key, long privateUserId, String json) {
        try {
            return Mono.just(objectMapper.readValue(json, CurrentUserContext.class));
        } catch (JacksonException exception) {
            LOGGER.warn("Invalid current-user cache value; evicting key={}", key, exception);
            return evict(privateUserId).then(Mono.empty());
        }
    }

    private String key(long privateUserId) {
        if (privateUserId <= 0) {
            throw new IllegalArgumentException("privateUserId must be positive");
        }

        return keyFactory.create(MODULE, RESOURCE, privateUserId);
    }
}
