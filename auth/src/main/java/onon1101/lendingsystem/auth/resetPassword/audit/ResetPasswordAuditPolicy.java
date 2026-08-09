package onon1101.lendingsystem.auth.resetPassword.audit;

import onon1101.lendingsystem.auth.commons.PasswordProperties;
import onon1101.lendingsystem.auth.resetPassword.ResetPasswordCommand;
import onon1101.lendingsystem.auth.resetPassword.ResetPasswordResult;
import onon1101.lendingsystem.configurations.audit.AuditEvent;
import onon1101.lendingsystem.configurations.audit.CommandAuditPolicy;
import onon1101.lendingsystem.configurations.audit.eventAttributes.TokenAuditEventAttribute;
import onon1101.lendingsystem.configurations.domain.Result;
import onon1101.lendingsystem.configurations.token.JwtTokenService;
import org.springframework.stereotype.Component;

@Component
public final class ResetPasswordAuditPolicy
        implements CommandAuditPolicy<
                ResetPasswordCommand, Result<ResetPasswordResult>, AuditEvent> {

    private final JwtTokenService tokenService;
    private final PasswordProperties passwordProperties;

    public ResetPasswordAuditPolicy(
            JwtTokenService tokenService, PasswordProperties passwordProperties) {
        this.tokenService = tokenService;
        this.passwordProperties = passwordProperties;
    }

    @Override
    public AuditEvent onReturned(ResetPasswordCommand command, Result<ResetPasswordResult> result) {
        return switch (result) {
            case Result.Success<ResetPasswordResult> success ->
                    new AuditEvent.Success(
                            "password_reset_receiver_successed", tokenAttribute(command));
            case Result.Failure<ResetPasswordResult> failure ->
                    new AuditEvent.Rejected(
                            "password_reset_receiver_failed",
                            tokenAttribute(command),
                            failure.error().code());
        };
    }

    @Override
    public AuditEvent onThrown(ResetPasswordCommand command, Throwable throwable) {
        return new AuditEvent.Failed(
                "password_reset_receiver_failed", tokenAttribute(command), "system_error");
    }

    private TokenAuditEventAttribute tokenAttribute(ResetPasswordCommand command) {
        return new TokenAuditEventAttribute(tokenService, passwordProperties, command.resetToken());
    }
}
