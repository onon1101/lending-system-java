package onon1101.lendingsystem.auth.emailVerificationResend;

import java.util.UUID;

public record EmailVerificationAccount(UUID publicUserId, String username, String email) {}
