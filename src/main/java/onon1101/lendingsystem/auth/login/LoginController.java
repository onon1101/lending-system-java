package onon1101.lendingsystem.auth.login;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import onon1101.lendingsystem.sharedkernel.api.ApiResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return loginService.login(
                request.username(),
                request.password()).match(
                result -> {
                    LoginResponse data = new LoginResponse(
                            result.accessToken(),
                            "Bearer",
                            result.expiresIn());
                    return ResponseEntity.ok(ApiResponse.success(
                            HttpStatus.OK,
                            data));
                },
                errorCode -> ResponseEntity.ok(ApiResponse.failure(
                        HttpStatus.OK,
                        errorCode)));
    }
}
