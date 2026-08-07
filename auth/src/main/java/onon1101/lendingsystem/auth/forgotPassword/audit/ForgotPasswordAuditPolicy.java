package onon1101.lendingsystem.auth.forgotPassword.audit;

import onon1101.lendingsystem.auth.forgotPassword.ForgotPasswordCommand;
import onon1101.lendingsystem.auth.forgotPassword.ForgotPasswordResult;
import onon1101.lendingsystem.sharedkernel.audit.AuditEvent;
import onon1101.lendingsystem.sharedkernel.audit.CommandAuditPolicy;
import onon1101.lendingsystem.sharedkernel.audit.EmailAuditEventAttribute;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
import org.springframework.stereotype.Component;

@Component
public final class ForgotPasswordAuditPolicy
        implements CommandAuditPolicy<
                ForgotPasswordCommand, Result<ForgotPasswordResult>, AuditEvent> {

    @Override
    public AuditEvent onReturned(
            ForgotPasswordCommand command, Result<ForgotPasswordResult> result) {
        return switch (result) {
            case Result.Success<ForgotPasswordResult> ignored ->
                    new AuditEvent.Success(
                            "password_reset_sender_requested",
                            new EmailAuditEventAttribute(command.email()));
            case Result.Failure<ForgotPasswordResult> failure ->
                    new AuditEvent.Rejected(
                            "password_reset_sender_rejected",
                            new EmailAuditEventAttribute(command.email()),
                            failure.error().message());
        };
    }

    @Override
    public AuditEvent onThrown(ForgotPasswordCommand command, Throwable throwable) {
        return new AuditEvent.Failed(
                "password_reset_sender_failed",
                new EmailAuditEventAttribute(command.email()),
                "system_error");
    }
}
