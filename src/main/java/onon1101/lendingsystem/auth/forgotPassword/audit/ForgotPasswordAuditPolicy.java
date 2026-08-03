package onon1101.lendingsystem.auth.forgotPassword.audit;

import java.util.Locale;
import onon1101.lendingsystem.sharedkernel.audit.CommandAuditPolicy;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
import org.springframework.stereotype.Component;

@Component
public class ForgotPasswordAuditPolicy implements CommandAuditPolicy<ForgotPasswordAuditEvent> {

    @Override
    public ForgotPasswordAuditEvent onReturned(Object[] arguments, Object result) {
        String normalizedEmail = normalizedEmail(arguments);
        Result<?> commandResult = (Result<?>) result;

        return switch (commandResult) {
            /*
             * 帳號不存在時 ForgotPasswordService 也會回傳成功，
             * 因此 audit 不會洩漏帳號是否存在。
             */
            case Result.Success<?> ignored ->
                    new ForgotPasswordAuditEvent.Requested(normalizedEmail);
            case Result.Failure<?> failure ->
                    new ForgotPasswordAuditEvent.Rejected(
                            normalizedEmail, reasonFor(failure.error().code()));
        };
    }

    @Override
    public ForgotPasswordAuditEvent onThrown(Object[] arguments, Throwable throwable) {
        return new ForgotPasswordAuditEvent.Failed(normalizedEmail(arguments), "system_error");
    }

    private String normalizedEmail(Object[] arguments) {
        if (arguments.length == 0 || !(arguments[0] instanceof String email) || email.isBlank()) {
            return "invalid";
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String reasonFor(String errorCode) {
        return switch (errorCode) {
            case "ForgotPassword.InvalidEmail" -> "invalid_email";
            default -> "business_error";
        };
    }
}
