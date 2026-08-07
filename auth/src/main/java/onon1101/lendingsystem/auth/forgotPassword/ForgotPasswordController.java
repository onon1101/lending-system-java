package onon1101.lendingsystem.auth.forgotPassword;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/v1/auth")
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    public ForgotPasswordController(ForgotPasswordService forgotPasswordService) {
        this.forgotPasswordService = forgotPasswordService;
    }

    @Operation(
            summary = "忘記密碼",
            description =
                    "依 Email 建立重設密碼流程並寄送重設密碼信。業務失敗時 errorCode 可能為"
                            + " ForgotPassword.InvalidEmail。")
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
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ForgotPasswordResponse>> handle(
            @Valid @RequestBody ForgotPasswordRequest request) {
        return forgotPasswordService
                .handle(new ForgotPasswordCommand(request.email()))
                .match(
                        result -> {
                            ForgotPasswordResponse response =
                                    new ForgotPasswordResponse("已發送 Email");

                            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, response));
                        },
                        errorCode ->
                                ResponseEntity.ok(ApiResponse.failure(HttpStatus.OK, errorCode)));
    }
}
