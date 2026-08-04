package onon1101.lendingsystem.auth.resetPassword;

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
public final class ResetPasswordController {

    private final ResetPasswordService service;

    public ResetPasswordController(
            ResetPasswordService service
    ) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<ApiResponse<ResetPasswordResponse>> resetPassword(
            @Valid
            @RequestBody
            ResetPasswordRequest request
    ) {
        return service
                .execute(new ResetPasswordCommand(request.resetToken(),
                        request.newPassword()))
                .match(
                        result -> ResponseEntity.ok(ApiResponse.success(
                                HttpStatus.OK, new ResetPasswordResponse("已修改密碼"))),
                        errorCode -> ResponseEntity.ok(
                                ApiResponse.failure(HttpStatus.OK, errorCode)
                        ));
    }
}
