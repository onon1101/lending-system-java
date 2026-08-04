package onon1101.lendingsystem.user.validateEmail;

import onon1101.lendingsystem.auth.resetPassword.ResetPasswordResponse;
import onon1101.lendingsystem.sharedkernel.api.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class ValidateEmailController {

    private final ValidateEmailService validateEmailService;

    public ValidateEmailController(
        ValidateEmailService validateEmailService
    ) {
        this.validateEmailService = validateEmailService;
    }

    @GetMapping("/validate-email?token={validateToken}")
    public ResponseEntity<ApiResponse<ValidateEmailResponse>> handle(
            @PathVariable String validateToken
    ) {
        return validateEmailService
                .execute(new ValidateEmailCommand(validateToken))
                .match(
                        result -> ResponseEntity.ok(ApiResponse.success(
                                HttpStatus.OK, new ValidateEmailResponse("已驗證電子郵件"))),
                        errorCode -> ResponseEntity.ok(
                                ApiResponse.failure(HttpStatus.OK, errorCode)
                        ));
    }
}
