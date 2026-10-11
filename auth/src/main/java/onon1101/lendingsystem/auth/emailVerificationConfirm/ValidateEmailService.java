package onon1101.lendingsystem.auth.emailVerificationConfirm;

import onon1101.lendingsystem.application.audit.AuditedCommand;
import onon1101.lendingsystem.auth.emailVerificationConfirm.audit.ValidateEmailAuditPolicy;
import onon1101.lendingsystem.auth.emailVerificationConfirm.error.InvalidEmailUpdatedDomainError;
import onon1101.lendingsystem.infrastructure.security.token.TokenPayload;
import onon1101.lendingsystem.infrastructure.security.token.emailvalidation.EmailValidateTokenService;
import onon1101.lendingsystem.shared.domain.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

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
    public Mono<Result<ValidateEmailResult>> execute(ValidateEmailCommand command) {
        String token = command.validateToken();

        TokenPayload payload = tokenService.decode(token);

        return validateEmailWriter
                .updateStateByPublicId(payload.publicUserId())
                .map(
                        updated ->
                                updated
                                        ? Result.success(new ValidateEmailResult())
                                        : Result.failure(new InvalidEmailUpdatedDomainError()));
    }
}
