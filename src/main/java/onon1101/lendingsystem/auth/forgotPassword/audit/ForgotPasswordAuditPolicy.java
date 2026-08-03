package onon1101.lendingsystem.auth.forgotPassword.audit;

import onon1101.lendingsystem.auth.forgotPassword.ForgotPasswordCommand;
import onon1101.lendingsystem.auth.forgotPassword.ForgotPasswordResult;
import onon1101.lendingsystem.sharedkernel.audit.CommandAuditPolicy;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
import org.springframework.stereotype.Component;

@Component
public final class ForgotPasswordAuditPolicy
        implements CommandAuditPolicy<
                ForgotPasswordCommand, Result<ForgotPasswordResult>, ForgotPasswordAuditEvent> {

    @Override
    public ForgotPasswordAuditEvent onReturned(
            ForgotPasswordCommand command, Result<ForgotPasswordResult> result) {
        String normalizedEmail = command.email();

        return switch (result) {
            /*
             * 帳號不存在時 ForgotPasswordService 也會回傳成功，
             * 因此 audit 不會洩漏帳號是否存在。
             */
            case Result.Success<ForgotPasswordResult> ignored ->
                    new ForgotPasswordAuditEvent.Requested(normalizedEmail);
            case Result.Failure<ForgotPasswordResult> failure ->
                    new ForgotPasswordAuditEvent.Rejected(
                            normalizedEmail, failure.error().code());
        };
    }

    @Override
    public ForgotPasswordAuditEvent onThrown(
            ForgotPasswordCommand command, Throwable throwable) {
        return new ForgotPasswordAuditEvent.Failed(command.email(), "system_error");
    }
}
