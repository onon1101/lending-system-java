package onon1101.lendingsystem.user.register;

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

@Tag(name = "user", description = "使用者服務相關 API")
@RestController
@RequestMapping("/api/v1/user")
public class RegisterController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Operation(
            summary = "註冊使用者",
            description =
                    "建立帳號並寄送 Email 驗證信。業務失敗時 errorCode 可能為 User.InvalidEmail 或"
                            + " User.InvalidRegistration。")
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
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        return registerService
                .register(
                        new RegisterCommand(
                                request.username(), request.password(), request.email()))
                .match(
                        result -> {
                            RegisterResponse data = new RegisterResponse(result.userId());
                            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, data));
                        },
                        errorCode ->
                                ResponseEntity.ok(ApiResponse.failure(HttpStatus.OK, errorCode)));
    }
}
