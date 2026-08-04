package onon1101.lendingsystem.auth.resetPassword.audit;

import onon1101.lendingsystem.auth.resetPassword.ResetPasswordCommand;
import onon1101.lendingsystem.auth.resetPassword.ResetPasswordRequest;
import onon1101.lendingsystem.auth.resetPassword.ResetPasswordResult;
import onon1101.lendingsystem.sharedkernel.audit.CommandAuditPolicy;

import onon1101.lendingsystem.sharedkernel.domain.result.Result;

import org.springframework.stereotype.Component;

@Component
public final class ResetPasswordAuditPolicy implements CommandAuditPolicy<ResetPasswordCommand, Result<ResetPasswordResult>, ResetPasswordAuditEvent> {

    @Override
    public ResetPasswordAuditEvent onReturned(ResetPasswordCommand command,
                                              Result<ResetPasswordResult> result) {
        return switch (result) {
            case Result.Success<ResetPasswordResult> success ->
                    new ResetPasswordAuditEvent.Succeeded(success
                            .value()
                            .email());
            case Result.Failure<ResetPasswordResult> failure ->
                    new ResetPasswordAuditEvent.Failed("unavailable", failure
                            .error()
                            .code());
        };
    }

    @Override
    public ResetPasswordAuditEvent onThrown(ResetPasswordCommand command,
                                            Throwable throwable) {
        return new ResetPasswordAuditEvent.Failed("unavailable", "system_error");
    }
}
