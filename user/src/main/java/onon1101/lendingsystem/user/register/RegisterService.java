package onon1101.lendingsystem.user.register;

import onon1101.lendingsystem.configurations.audit.AuditedCommand;
import onon1101.lendingsystem.configurations.domain.Result;
import onon1101.lendingsystem.configurations.email.EmailUtil;
import onon1101.lendingsystem.configurations.token.emailvalidation.EmailValidateTokenService;
import onon1101.lendingsystem.user.register.audit.RegistrationAuditPolicy;
import onon1101.lendingsystem.user.register.email.EmailValidateRequested;
import onon1101.lendingsystem.user.register.error.InvalidEmailDomainError;
import onon1101.lendingsystem.user.register.error.InvalidRegistrationDomainError;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalEventPublisher;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class RegisterService {

    private final RegisterAccountWriter accountWriter;
    private final PasswordEncoder passwordEncoder;
    private final TransactionalEventPublisher eventPublisher;
    private final EmailValidateTokenService emailValidateTokenService;

    public RegisterService(
            RegisterAccountWriter accountWriter,
            PasswordEncoder passwordEncoder,
            TransactionalEventPublisher eventPublisher,
            EmailValidateTokenService emailValidateTokenService) {
        this.accountWriter = accountWriter;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
        this.emailValidateTokenService = emailValidateTokenService;
    }

    @Transactional
    @AuditedCommand(RegistrationAuditPolicy.class)
    public Mono<Result<RegisterResult>> register(RegisterCommand command) {
        String username = command.username();
        String email = command.email();
        String password = command.password();

        if (EmailUtil.validateEmail(email)) {
            return Mono.just(Result.failure(new InvalidEmailDomainError()));
        }

        return Mono.fromCallable(() -> passwordEncoder.encode(password))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(encoded -> accountWriter.registerAccount(username, encoded, email))
                .flatMap(
                        account -> {
                            String token =
                                    emailValidateTokenService.createToken(
                                            account.publicUserId(), username);
                            return eventPublisher
                                    .publishEvent(
                                            new EmailValidateRequested(email, username, token))
                                    .thenReturn(
                                            Result.success(
                                                    new RegisterResult(account.publicUserId())));
                        })
                .switchIfEmpty(
                        Mono.fromSupplier(
                                () -> Result.failure(new InvalidRegistrationDomainError())));
    }
}
