package onon1101.lendingsystem.auth.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationAuditLogger {

    private static final Logger AUDIT_LOGGER = LoggerFactory.getLogger("SECURITY_AUDIT");
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationAuditLogger.class);

    private final AccountReferenceEncoder accountReferenceEncoder;

    public AuthenticationAuditLogger(AccountReferenceEncoder accountReferenceEncoder) {
        this.accountReferenceEncoder = accountReferenceEncoder;
    }

    public void loginFailed(String normalizedUsername) {
        String accountRef = encodeAccountReference(normalizedUsername);

        AUDIT_LOGGER.warn("event=authentication_failed accountRef={} outcome=denied", accountRef);
    }

    public void loginSuccess(String normalizedUsername) {
        String accountRef = encodeAccountReference(normalizedUsername);

        AUDIT_LOGGER.info(
                "event=authentication_succeeded accountRef={} outcome=allowed", accountRef);
    }

    private String encodeAccountReference(String normalizedUsername) {
        try {
            return accountReferenceEncoder.encode(normalizedUsername);
        } catch (RuntimeException exception) {
            LOGGER.error("Could not encode account reference for authentication audit", exception);
            return "unavailable";
        }
    }
}
