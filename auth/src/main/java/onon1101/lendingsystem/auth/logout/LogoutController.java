package onon1101.lendingsystem.auth.logout;

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
@RequestMapping("/api/v1/auth")
public class LogoutController {

    private final LogoutService logoutService;

    public LogoutController(LogoutService logoutService) {
        this.logoutService = logoutService;
    }

    @Operation(
            summary = "使用者登出",
            description = "用於將 refresh-token 刪除，雖然使用者登出後，依然可以用 access token 登出。" +
                    "但是仍舊過有效期限後，就會失效。"
    )
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid
            @RequestBody
            LogoutRequest request
    ) {
        logoutService.logout(
                new LogoutCommand(request.refreshToken())
        );

        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK, null));
    }
}
