package onon1101.lendingsystem.auth.emailVerificationConfirm;

import onon1101.lendingsystem.configurations.audit.AuditedCommand;
import onon1101.lendingsystem.configurations.domain.Result;
import onon1101.lendingsystem.configurations.token.TokenPayload;
import onon1101.lendingsystem.configurations.token.emailvalidation.EmailValidateTokenService;
import onon1101.lendingsystem.auth.emailVerificationConfirm.audit.ValidateEmailAuditPolicy;
import onon1101.lendingsystem.auth.emailVerificationConfirm.error.InvalidEmailUpdatedDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ValidateEmailService {

    private final ValidateEmailWriter validateEmailWriter;
    private final EmailValidateTokenService tokenService;

    public ValidateEmailService(
            ValidateEmailWriter validateEmailWriter, EmailValidateTokenService tokenService) {
        this.validateEmailWriter = validateEmailWriter;
        this.tokenService = tokenService;
    }

    @Transactional()
    @AuditedCommand(ValidateEmailAuditPolicy.class)
    public Result<ValidateEmailResult> execute(ValidateEmailCommand command) {
        String token = command.validateToken();

        TokenPayload payload = tokenService.decode(token);

        boolean updated = validateEmailWriter.updateStateByPublicId(payload.publicUserId());

        if (!updated) {
            return Result.failure(new InvalidEmailUpdatedDomainError());
        }

        return Result.success(new ValidateEmailResult());
    }
}
