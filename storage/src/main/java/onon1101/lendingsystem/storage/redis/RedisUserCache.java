package onon1101.lendingsystem.storage.redis;

import java.util.Optional;
import onon1101.lendingsystem.sharedkernel.context.user.CurrentUserContext;
import onon1101.lendingsystem.sharedkernel.context.user.UserCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Repository
public class RedisUserCache implements UserCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisUserCache.class);
    private static final String MODULE = "user";
    private static final String RESOURCE = "context";

    private final StringRedisTemplate redisTemplate;
    private final RedisKeyFactory keyFactory;
    private final ObjectMapper objectMapper;
    private final UserCacheProperties properties;

    public RedisUserCache(
            StringRedisTemplate redisTemplate,
            RedisKeyFactory keyFactory,
            ObjectMapper objectMapper,
            UserCacheProperties properties) {
        this.redisTemplate = redisTemplate;
        this.keyFactory = keyFactory;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    @Override
    public Optional<CurrentUserContext> find(long privateUserId) {
        String key = key(privateUserId);

        try {
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) {
                return Optional.empty();
            }

            return Optional.of(objectMapper.readValue(json, CurrentUserContext.class));
        } catch (JacksonException exception) {
            LOGGER.warn("Invalid current-user cache value; evicting key={}", key, exception);
            evict(privateUserId);
            return Optional.empty();
        } catch (DataAccessException exception) {
            LOGGER.warn("Unable to read current-user cache key={}", key, exception);
            return Optional.empty();
        }
    }

    @Override
    public void save(CurrentUserContext user) {
        String key = key(user.privateUserId());

        try {
            String json = objectMapper.writeValueAsString(user);
            redisTemplate.opsForValue().set(key, json, properties.ttl());
        } catch (JacksonException exception) {
            throw new IllegalStateException("Unable to serialize current-user cache", exception);
        } catch (DataAccessException exception) {
            LOGGER.warn("Unable to write current-user cache key={}", key, exception);
        }
    }

    @Override
    public void evict(long privateUserId) {
        String key = key(privateUserId);

        try {
            redisTemplate.delete(key);
        } catch (DataAccessException exception) {
            LOGGER.warn("Unable to evict current-user cache key={}", key, exception);
        }
    }

    private String key(long privateUserId) {
        if (privateUserId <= 0) {
            throw new IllegalArgumentException("privateUserId must be positive");
        }

        return keyFactory.create(MODULE, RESOURCE, privateUserId);
    }
}
