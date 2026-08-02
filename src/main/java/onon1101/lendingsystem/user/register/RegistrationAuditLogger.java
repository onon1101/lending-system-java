package onon1101.lendingsystem.user.register;

import java.util.UUID;
import onon1101.lendingsystem.sharedkernel.AccountReferenceEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RegistrationAuditLogger {

    private static final Logger AUDIT_LOGGER = LoggerFactory.getLogger("SECURITY_AUDIT");
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationAuditLogger.class);

    private final AccountReferenceEncoder accountReferenceEncoder;

    public RegistrationAuditLogger(AccountReferenceEncoder accountReferenceEncoder) {
        this.accountReferenceEncoder = accountReferenceEncoder;
    }

    public void registerFailed(String normalizedUsername, String reason) {
        String accountRef = encodeAccountReference(normalizedUsername);

        AUDIT_LOGGER.warn(
                "event=registration_failed accountRef={} outcome=denied reason={}",
                accountRef,
                reason);
    }

    public void registerSuccess(String normalizedUsername, UUID publicUserId) {
        String accountRef = encodeAccountReference(normalizedUsername);

        AUDIT_LOGGER.info(
                "event=registration_succeeded accountRef={} userId={} outcome=allowed",
                accountRef,
                publicUserId);
    }

    private String encodeAccountReference(String normalizedUsername) {
        try {
            return accountReferenceEncoder.encode(normalizedUsername);
        } catch (RuntimeException exception) {
            LOGGER.error("Could not encode account reference for registration audit", exception);
            AUDIT_LOGGER.error(
                    "event=registration_audit_degraded accountRef=unavailable outcome=error"
                            + " reason=account_reference_encoding_failed");
            return "unavailable";
        }
    }
}
