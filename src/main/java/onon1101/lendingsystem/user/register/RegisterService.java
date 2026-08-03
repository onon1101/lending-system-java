package onon1101.lendingsystem.user.register;

import java.util.Locale;
import onon1101.lendingsystem.sharedkernel.EmailUtil;
import onon1101.lendingsystem.sharedkernel.audit.AuditedCommand;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
import onon1101.lendingsystem.user.register.audit.RegistrationAuditPolicy;
import onon1101.lendingsystem.user.register.error.InvalidEmailDomainError;
import onon1101.lendingsystem.user.register.error.InvalidRegistrationDomainError;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterService {

    private final RegisterAccountWriter accountWriter;
    private final PasswordEncoder passwordEncoder;

    public RegisterService(RegisterAccountWriter accountWriter, PasswordEncoder passwordEncoder) {
        this.accountWriter = accountWriter;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @AuditedCommand(RegistrationAuditPolicy.class)
    public Result<RegisterResult> register(RegisterCommand command) {
        String username = command.username();
        String email = command.email();
        String password = command.password();

        if (!EmailUtil.validateEmail(email)) {
            return Result.failure(new InvalidEmailDomainError());
        }

        String passwordEncoded = passwordEncoder.encode(password);

        // todo: 需要寄信驗證

        RegisterAccount account =
                accountWriter
                        .registerAccount(username, passwordEncoded, email)
                        .orElse(null);

        if (account == null) {
            return Result.failure(new InvalidRegistrationDomainError());
        }

        return Result.success(new RegisterResult(account.publicUserId()));
    }
}
