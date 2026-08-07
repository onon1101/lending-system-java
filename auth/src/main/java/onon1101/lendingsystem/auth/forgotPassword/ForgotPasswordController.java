package onon1101.lendingsystem.auth.forgotPassword;

import jakarta.validation.Valid;

import onon1101.lendingsystem.sharedkernel.api.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    public ForgotPasswordController(
            ForgotPasswordService forgotPasswordService
    ) {
        this.forgotPasswordService = forgotPasswordService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ForgotPasswordResponse>> handle(
            @Valid
            @RequestBody
            ForgotPasswordRequest request
    ) {
        return forgotPasswordService
                .handle(new ForgotPasswordCommand(request.email()))
                .match(
                        result -> {
                            ForgotPasswordResponse response =
                                    new ForgotPasswordResponse("已發送 Email");

                            return ResponseEntity.ok(ApiResponse.success(
                                    HttpStatus.OK,
                                    response));
                        },
                        errorCode -> ResponseEntity.ok(
                                ApiResponse.failure(HttpStatus.OK, errorCode)));
    }
}
