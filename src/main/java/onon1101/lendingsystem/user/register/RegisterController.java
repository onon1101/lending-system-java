package onon1101.lendingsystem.user.register;

import jakarta.validation.Valid;
import onon1101.lendingsystem.sharedkernel.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class RegisterController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        return registerService
                .register(new RegisterCommand(request.username(), request.password(), request.email()))
                .match(
                        result -> {
                            RegisterResponse data = new RegisterResponse(result.userId());
                            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, data));
                        },
                        errorCode ->
                                ResponseEntity.ok(ApiResponse.failure(HttpStatus.OK, errorCode)));
    }
}
