package onon1101.lendingsystem.user.validateEmail;

import onon1101.lendingsystem.sharedkernel.audit.AuditedCommand;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;

import onon1101.lendingsystem.sharedkernel.token.TokenPayload;
import onon1101.lendingsystem.user.commons.EmailValidateTokenService;

import onon1101.lendingsystem.user.validateEmail.audit.ValidateEmailAuditPolicy;
import onon1101.lendingsystem.user.validateEmail.error.InvalidEmailUpdatedDomainError;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ValidateEmailService {

    private final ValidateEmailWriter validateEmailWriter;
    private final EmailValidateTokenService tokenService;

    public ValidateEmailService(
            ValidateEmailWriter validateEmailWriter,
            EmailValidateTokenService tokenService
    ) {
        this.validateEmailWriter = validateEmailWriter;
        this.tokenService = tokenService;
    }

    @Transactional()
    @AuditedCommand(ValidateEmailAuditPolicy.class)
    public Result<ValidateEmailResult> execute(
            ValidateEmailCommand command
    ) {
        String token = command.validateToken();

        TokenPayload payload = tokenService.decode(token);

        boolean updated = validateEmailWriter.updateStateByPublicId(payload.publicUserId());

        if (!updated) {
            return Result.failure(new InvalidEmailUpdatedDomainError());
        }

        return Result.success(new ValidateEmailResult());
    }
}
