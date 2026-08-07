package onon1101.lendingsystem.auth.emailVerificationConfirm;

import onon1101.lendingsystem.sharedkernel.audit.AuditedCommand;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
import onon1101.lendingsystem.sharedkernel.token.TokenPayload;
import onon1101.lendingsystem.sharedkernel.token.emailvalidation.EmailValidateTokenService;
import onon1101.lendingsystem.auth.emailVerificationConfirm.audit.ValidateEmailAuditPolicy;
import onon1101.lendingsystem.auth.emailVerificationConfirm.error.InvalidEmailUpdatedDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

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

        // todo: 所有的 failure 都需要 rollback
        if (!updated) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failure(new InvalidEmailUpdatedDomainError());
        }

        return Result.success(new ValidateEmailResult());
    }
}
