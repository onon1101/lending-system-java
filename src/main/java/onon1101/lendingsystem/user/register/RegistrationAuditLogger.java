package onon1101.lendingsystem.user.register;

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

    public void registerFailed(String normalizedUsername) {
        String accountRef = encodeAccountReference(normalizedUsername);

        AUDIT_LOGGER.warn("event=registration_failed accountRef={} outcome=denied", accountRef);
    }

    public void registerSuccess(String normalizedUsername) {
        String accountRef = encodeAccountReference(normalizedUsername);

        AUDIT_LOGGER.info("event=registration_succeeded accountRef={} outcome=allowed", accountRef);
    }

    private String encodeAccountReference(String normalizedUsername) {
        try {
            return accountReferenceEncoder.encode(normalizedUsername);
        } catch (RuntimeException exception) {
            LOGGER.error("Could not encode account reference for registration audit", exception);
            return "unavailable";
        }
    }
}
