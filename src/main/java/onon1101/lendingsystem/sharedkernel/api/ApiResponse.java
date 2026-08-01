package onon1101.lendingsystem.sharedkernel.api;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        int code,
        boolean isSuccess,
        String errorCode,
        T data) {

    public static <T> ApiResponse<T> success(
            HttpStatus status,
            T data) {
        return new ApiResponse<>(
                status.value(),
                true,
                null,
                data);
    }

    public static <T> ApiResponse<T> failure(
            HttpStatus status,
            String errorCode) {
        return new ApiResponse<>(
                status.value(),
                false,
                errorCode,
                null);
    }
}
