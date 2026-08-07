package onon1101.lendingsystem.auth.resetPassword;

import java.time.Instant;
import java.util.UUID;

public interface ResetPasswordWriter {

    boolean updatePassword(
            UUID publicUserId,
            String encodedPassword,
            Instant tokenIssuedAt
    );
}
