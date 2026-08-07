package onon1101.lendingsystem.configurations.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException exception) {
        return failure(HttpStatus.BAD_REQUEST, "Validation.InvalidRequest");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiResponse<Void>> handleUnreadableMessageException(
            HttpMessageNotReadableException exception) {
        return failure(HttpStatus.BAD_REQUEST, "Request.MalformedBody");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(
            NoResourceFoundException exception) {
        return failure(HttpStatus.NOT_FOUND, "Resource.NotFound");
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception exception) {
        LOGGER.error("Unhandled exception while processing request", exception);
        return failure(HttpStatus.INTERNAL_SERVER_ERROR, "System.InternalServerError");
    }

    @ExceptionHandler(CannotGetJdbcConnectionException.class)
    public ResponseEntity<ApiResponse<Void>> handleDatabaseUnavailable(
            CannotGetJdbcConnectionException exception) {
        return failure(HttpStatus.SERVICE_UNAVAILABLE, "Database.Unavailable");
    }

    private ResponseEntity<ApiResponse<Void>> failure(HttpStatus status, String errorCode) {
        return ResponseEntity.status(status).body(ApiResponse.failure(status, errorCode));
    }
}
