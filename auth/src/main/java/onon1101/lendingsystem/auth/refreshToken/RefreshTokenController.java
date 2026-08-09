package onon1101.lendingsystem.auth.refreshToken;

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
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    public RefreshTokenController(
            RefreshTokenService refreshTokenService
    ) {
        this.refreshTokenService = refreshTokenService;
    }

    @Operation(
            summary = "更新 Access Token",
            description =
                    "使用 refresh token 取得新的 access token 與 refresh token。"
                            + "舊 refresh token 在使用後立即失效。"
                            + "業務失敗仍回傳 HTTP 200，並以 errorCode 表示失敗原因。"
    )
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        return refreshTokenService
                .refresh(
                        new RefreshTokenCommand(
                                request.refreshToken()
                        )
                )
                .match(
                        result -> {
                            RefreshTokenResponse response =
                                    new RefreshTokenResponse(
                                            result.accessToken(),
                                            "Bearer",
                                            result.accessTokenExpiresIn(),
                                            result.refreshToken(),
                                            result.refreshTokenExpiresIn()
                                    );

                            return ResponseEntity.ok(
                                    ApiResponse.success(
                                            HttpStatus.OK,
                                            response
                                    )
                            );
                        },
                        errorCode -> ResponseEntity.ok(
                                ApiResponse.failure(
                                        HttpStatus.OK,
                                        errorCode
                                )
                        )
                );
    }
}
