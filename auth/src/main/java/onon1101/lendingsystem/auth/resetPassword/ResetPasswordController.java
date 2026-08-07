package onon1101.lendingsystem.auth.resetPassword;

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
@RequestMapping("/api/v1/auth")
public class ResetPasswordController {

    private final ResetPasswordService service;

    public ResetPasswordController(ResetPasswordService service) {
        this.service = service;
    }

    @Operation(
            summary = "重設密碼",
            description =
                    "使用重設密碼 token 與新密碼完成密碼變更。業務失敗時 errorCode 可能為"
                            + " ResetPassword.InvalidResetToken。")
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
    @PostMapping
    ResponseEntity<ApiResponse<ResetPasswordResponse>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        return service.execute(
                        new ResetPasswordCommand(request.resetToken(), request.newPassword()))
                .match(
                        result ->
                                ResponseEntity.ok(
                                        ApiResponse.success(
                                                HttpStatus.OK, new ResetPasswordResponse("已修改密碼"))),
                        errorCode ->
                                ResponseEntity.ok(ApiResponse.failure(HttpStatus.OK, errorCode)));
    }
}
