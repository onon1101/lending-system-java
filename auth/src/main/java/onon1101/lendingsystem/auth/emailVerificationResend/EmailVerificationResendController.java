package onon1101.lendingsystem.auth.emailVerificationResend;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import onon1101.lendingsystem.configurations.controller.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "auth", description = "認證相關 API")
@RestController
@RequestMapping("/api/v1/auth/email-verification")
public class EmailVerificationResendController {

    private static final String GENERIC_MESSAGE = "若此 Email 尚未完成驗證，系統將寄送驗證信";

    private final EmailVerificationResendService service;

    public EmailVerificationResendController(EmailVerificationResendService service) {
        this.service = service;
    }

    @Operation(
            summary = "重送 Email 驗證信",
            description =
                    "若 Email 對應到尚未驗證的有效帳號，系統將重新寄送驗證信。"
                            + "為避免帳號枚舉，所有帳號狀態皆回傳相同訊息。")
    @PostMapping("/resend")
    public ResponseEntity<ApiResponse<ResendEmailVerificationResponse>> resend(
            @Valid @RequestBody ResendEmailVerificationRequest request) {
        service.resend(new ResendEmailVerificationCommand(request.email()));

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK, new ResendEmailVerificationResponse(GENERIC_MESSAGE)));
    }
}
