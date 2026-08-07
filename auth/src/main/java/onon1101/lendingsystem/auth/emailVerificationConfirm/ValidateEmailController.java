package onon1101.lendingsystem.auth.emailVerificationConfirm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import onon1101.lendingsystem.sharedkernel.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "auth", description = "認證相關 API")
@RestController
@RequestMapping("/api/v1/auth/email-verification")
public class ValidateEmailController {

    private final ValidateEmailService validateEmailService;

    public ValidateEmailController(ValidateEmailService validateEmailService) {
        this.validateEmailService = validateEmailService;
    }

    @Operation(
            summary = "確認 Email 驗證",
            description =
                    "使用 Email 驗證 token 完成電子郵件驗證。"
                            + "業務失敗時 errorCode 可能為 User.InvalidEmailUpdated。")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "請求已處理；業務成功或失敗由 isSuccess 與 errorCode 判斷",
                useReturnTypeSchema = true),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "請求格式或欄位驗證失敗",
                content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "系統未預期錯誤",
                content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "503",
                description = "資料庫暫時無法連線",
                content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<ValidateEmailResponse>> confirm(
            @Valid @RequestBody ConfirmEmailRequest request) {
        return validateEmailService
                .execute(new ValidateEmailCommand(request.token()))
                .match(
                        result ->
                                ResponseEntity.ok(
                                        ApiResponse.success(
                                                HttpStatus.OK,
                                                new ValidateEmailResponse("已驗證電子郵件"))),
                        errorCode ->
                                ResponseEntity.ok(ApiResponse.failure(HttpStatus.OK, errorCode)));
    }
}
