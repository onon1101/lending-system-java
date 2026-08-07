package onon1101.lendingsystem.auth.emailVerificationConfirm.audit;

import onon1101.lendingsystem.configurations.audit.AuditEvent;
import onon1101.lendingsystem.configurations.audit.CommandAuditPolicy;
import onon1101.lendingsystem.configurations.audit.TokenAuditEventAttribute;
import onon1101.lendingsystem.configurations.domain.Result;
import onon1101.lendingsystem.configurations.token.JwtTokenService;
import onon1101.lendingsystem.configurations.token.emailvalidation.EmailValidateTokenProperties;
import onon1101.lendingsystem.auth.emailVerificationConfirm.ValidateEmailCommand;
import onon1101.lendingsystem.auth.emailVerificationConfirm.ValidateEmailResult;
import org.springframework.stereotype.Component;

@Component
public class ValidateEmailAuditPolicy
        implements CommandAuditPolicy<
                ValidateEmailCommand, Result<ValidateEmailResult>, AuditEvent> {

    private final JwtTokenService tokenService;
    private final EmailValidateTokenProperties emailProperties;

    public ValidateEmailAuditPolicy(
            JwtTokenService jwtTokenService, EmailValidateTokenProperties emailProperties) {
        this.emailProperties = emailProperties;
        this.tokenService = jwtTokenService;
    }

    @Override
    public AuditEvent onReturned(ValidateEmailCommand command, Result<ValidateEmailResult> result) {
        return switch (result) {
            case Result.Success<ValidateEmailResult> success ->
                    new AuditEvent.Success("validate_email_successfully", tokenAttribute(command));
            case Result.Failure<ValidateEmailResult> failure ->
                    new AuditEvent.Rejected(
                            "validate_email_failed",
                            tokenAttribute(command),
                            failure.error().code());
        };
    }

    @Override
    public AuditEvent onThrown(ValidateEmailCommand command, Throwable throwable) {
        return new AuditEvent.Failed(
                "validate_email_failed", tokenAttribute(command), "system_error");
    }

    private TokenAuditEventAttribute tokenAttribute(ValidateEmailCommand command) {
        return new TokenAuditEventAttribute(tokenService, emailProperties, command.validateToken());
    }
}
