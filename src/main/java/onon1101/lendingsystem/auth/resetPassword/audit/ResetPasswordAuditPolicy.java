package onon1101.lendingsystem.auth.resetPassword.audit;

import onon1101.lendingsystem.auth.resetPassword.ResetPasswordCommand;
import onon1101.lendingsystem.auth.resetPassword.ResetPasswordResult;
import onon1101.lendingsystem.sharedkernel.audit.AuditEvent;
import onon1101.lendingsystem.sharedkernel.audit.CommandAuditPolicy;
import onon1101.lendingsystem.sharedkernel.audit.EmailAuditEventAttribute;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
import org.springframework.stereotype.Component;

@Component
public final class ResetPasswordAuditPolicy
        implements CommandAuditPolicy<
                ResetPasswordCommand, Result<ResetPasswordResult>, AuditEvent> {

    @Override
    public AuditEvent onReturned(
            ResetPasswordCommand command, Result<ResetPasswordResult> result) {
        return switch (result) {
            case Result.Success<ResetPasswordResult> success ->
                    new AuditEvent.Success(
                            "password_reset_receiver_successed",
                            new EmailAuditEventAttribute(success.value().email()));
            case Result.Failure<ResetPasswordResult> failure ->
                    new AuditEvent.Rejected(
                            "password_reset_receiver_failed",
                            new EmailAuditEventAttribute("unavailable"),
                            failure.error().code());
        };
    }

    @Override
    public AuditEvent onThrown(ResetPasswordCommand command, Throwable throwable) {
        return new AuditEvent.Failed(
                "password_reset_receiver_failed",
                new EmailAuditEventAttribute("unavailable"),
                "system_error");
    }
}
