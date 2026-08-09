package onon1101.lendingsystem.auth.emailVerificationResend.redis;

import java.time.Duration;
import java.util.UUID;
import onon1101.lendingsystem.configurations.redis.RedisKeyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisEmailVerificationResendThrottle
        implements EmailVerificationResendThrottle {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(RedisEmailVerificationResendThrottle.class);

    private final StringRedisTemplate redisTemplate;
    private final RedisKeyFactory keyFactory;
    private final Duration cooldown;

    public RedisEmailVerificationResendThrottle(
            StringRedisTemplate redisTemplate,
            RedisKeyFactory keyFactory,
            @Value("${lending.email-verification.resend-cooldown:PT1M}") Duration cooldown) {
        this.redisTemplate = redisTemplate;
        this.keyFactory = keyFactory;
        this.cooldown = cooldown;
    }

    @Override
    public boolean acquire(UUID publicUserId) {
        String key = keyFactory.create("auth", "email-verification-resend", publicUserId);

        try {
            return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, "1", cooldown));
        } catch (DataAccessException exception) {
            LOGGER.warn("Unable to acquire email-verification resend throttle", exception);
            return false;
        }
    }
}
