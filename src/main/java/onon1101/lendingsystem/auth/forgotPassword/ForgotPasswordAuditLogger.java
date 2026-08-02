package onon1101.lendingsystem.auth.forgotPassword;

import onon1101.lendingsystem.security.AccountReferenceEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ForgotPasswordAuditLogger {

    private static final Logger AUDIT_LOGGER =
            LoggerFactory.getLogger("SECURITY_AUDIT");

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    ForgotPasswordAuditLogger.class
            );

    private final AccountReferenceEncoder accountReferenceEncoder;

    public ForgotPasswordAuditLogger(
            AccountReferenceEncoder accountReferenceEncoder
    ) {
        this.accountReferenceEncoder = accountReferenceEncoder;
    }

    /*
    * 建議記錄三種結果：
password_reset_requested：請求格式有效，無論帳號是否存在
password_reset_rejected：email 格式錯誤
password_reset_failed：系統例外
     */
    public void passwordResetRequested(
            String normalizedEmail
    ) {
        AUDIT_LOGGER.info(
                "event=password_reset_requested " +
                        "accountRef={} outcome=accepted",
                encodeAccountReference(normalizedEmail)
        );
    }

    public void passwordResetRejected(
            String normalizedEmail,
            String reason
    ) {
        AUDIT_LOGGER.warn(
                "event=password_reset_rejected " +
                        "accountRef={} outcome=denied reason={}",
                encodeAccountReference(normalizedEmail),
                reason
        );
    }

    public void passwordResetFailed(
            String normalizedEmail,
            String reason
    ) {
        AUDIT_LOGGER.error(
                "event=password_reset_failed " +
                        "accountRef={} outcome=error reason={}",
                encodeAccountReference(normalizedEmail),
                reason
        );
    }

    private String encodeAccountReference(
            String normalizedEmail
    ) {
        try {
            return accountReferenceEncoder.encode(normalizedEmail);
        } catch (RuntimeException exception) {
            LOGGER.error(
                    "Could not encode account reference " +
                            "for password reset audit",
                    exception
            );

            return "unavailable";
        }
    }
}
