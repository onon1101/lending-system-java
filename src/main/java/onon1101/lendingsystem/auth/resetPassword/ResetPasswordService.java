package onon1101.lendingsystem.auth.resetPassword;

import onon1101.lendingsystem.auth.commons.PasswordTokenService;
import onon1101.lendingsystem.auth.commons.TokenPayload;
import onon1101.lendingsystem.auth.resetPassword.error.InvalidResetTokenDomainError;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResetPasswordService {

    private final PasswordTokenService tokenService;
    private final ResetPasswordWriter passwordWriter;
    private final PasswordEncoder passwordEncoder;

    public ResetPasswordService(
            PasswordTokenService tokenService,
            ResetPasswordWriter passwordWriter,
            PasswordEncoder passwordEncoder
    ) {
        this.passwordEncoder = passwordEncoder;
        this.passwordWriter = passwordWriter;
        this.tokenService = tokenService;
    }

    @Transactional
    public Result<ResetPasswordResult> execute(
            ResetPasswordCommand command
    ) {
        TokenPayload payload =
                tokenService.decode(
                        command.resetToken());

        String encodedPassword = passwordEncoder.encode(command.newPassword());

        boolean updated = passwordWriter.updatePassword(
                payload.publicUserId(),
                encodedPassword,
                payload.issuedAt());

        if (!updated) {
            return Result.failure(new InvalidResetTokenDomainError());
        }

        return Result.success(null);
    }
}
