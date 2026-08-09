package onon1101.lendingsystem.auth.emailVerificationResend.redis;

import java.util.UUID;

public interface EmailVerificationResendThrottle {

    boolean acquire(UUID publicUserId);
}
