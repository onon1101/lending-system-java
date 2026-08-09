package onon1101.lendingsystem.auth.logout;

import jakarta.validation.Valid;

import onon1101.lendingsystem.configurations.controller.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class LogoutController {

    private final LogoutService logoutService;

    public LogoutController(LogoutService logoutService) {
        this.logoutService = logoutService;
    }

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
