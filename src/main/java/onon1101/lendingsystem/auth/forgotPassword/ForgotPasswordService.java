package onon1101.lendingsystem.auth.forgotPassword;

import onon1101.lendingsystem.auth.token.JwtTokenService;
import onon1101.lendingsystem.sharedkernel.EmailUtil;
import onon1101.lendingsystem.sharedkernel.domain.result.DomainError;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;

import java.util.Locale;

public class ForgotPasswordService {

    private static final DomainError INVALID_EMAIL =
            new DomainError("ForgotPassword.InvalidEmail",
                    "The provided email address is invalid");

    private final ForgotPasswordAccountReader accountReader;
    private final JwtTokenService tokenService;

    public ForgotPasswordService(
            ForgotPasswordAccountReader accountReader,
            JwtTokenService tokenService) {
        this.accountReader = accountReader;
        this.tokenService = tokenService;
    }

    public Result<ForgotPasswordResult> handle(String email) {
        String normalizedEmail = email
                .trim()
                .toLowerCase(Locale.ROOT);

        if (!EmailUtil.validateEmail(normalizedEmail)) {
            return Result.failure(INVALID_EMAIL);
        }

        // read account
        ForgotPasswordAccount account = accountReader
                .findByEmail(normalizedEmail)
                .orElse(null);

        if (account == null) {
            // 不管帳號存在與否，都成功
            return Result.success(new ForgotPasswordResult());
        }

        // todo: 寄信功能應該與授權 token 實作分開來
        String accessToken = tokenService.createToken(account.publicUserId(),
                account.username());

        //todo: send email

        // return
        return Result.success(new ForgotPasswordResult());
    }
}
