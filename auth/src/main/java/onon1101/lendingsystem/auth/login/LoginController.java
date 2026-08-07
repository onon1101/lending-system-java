package onon1101.lendingsystem.auth.login;

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
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Operation(
            summary = "登入",
            description =
                    "使用帳號與密碼登入。業務失敗時仍回傳 HTTP 200，並在 errorCode 帶回"
                            + " Auth.InvalidCredentials 或 Auth" +
                            ".TooManyAttempts。")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "請求已處理；業務成功或失敗由 isSuccess 與 errorCode 判斷",
                    useReturnTypeSchema = true),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "請求格式或欄位驗證失敗",
                    content = @Content(schema = @Schema(implementation =
                            ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "系統未預期錯誤",
                    content = @Content(schema = @Schema(implementation =
                            ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "503",
                    description = "資料庫暫時無法連線",
                    content = @Content(schema = @Schema(implementation =
                            ApiResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        return loginService
                .login(new LoginCommand(request.username(), request.password()))
                .match(
                        result -> {
                            LoginResponse data =
                                    new LoginResponse(
                                            result.accessToken(),
                                            "Bearer",
                                            result.accessTokenExpiresIn(),
                                            result.refreshToken(),
                                            result.refreshTokenExpiresIn());
                            return ResponseEntity.ok(
                                    ApiResponse.success(HttpStatus.OK, data));
                        },
                        errorCode ->
                                ResponseEntity.ok(
                                        ApiResponse.failure(HttpStatus.OK,
                                                errorCode)));
    }
}
