package onon1101.lendingsystem.sharedkernel.api;

import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.http.HttpStatus;

@Schema(description = "API 標準回應")
public record ApiResponse<T>(
        @Schema(description = "HTTP 狀態碼", example = "200") int code,
        @Schema(description = "是否成功", example = "true") boolean isSuccess,
        @Schema(description = "業務錯誤代碼；成功時為 null", example = "Auth.InvalidCredentials")
                String errorCode,
        @Schema(description = "回應資料；失敗時為 null") T data) {

    public static <T> ApiResponse<T> success(HttpStatus status, T data) {
        return new ApiResponse<>(status.value(), true, null, data);
    }

    public static <T> ApiResponse<T> failure(HttpStatus status, String errorCode) {
        return new ApiResponse<>(status.value(), false, errorCode, null);
    }
}
