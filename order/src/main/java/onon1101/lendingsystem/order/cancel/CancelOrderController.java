package onon1101.lendingsystem.order.cancel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import onon1101.lendingsystem.order.cancel.error.CancelOrderDomainError;
import onon1101.lendingsystem.web.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Tag(name = "order", description = "借閱紀錄相關 API")
@RestController
@RequestMapping("/api/v1/orders")
public class CancelOrderController {

    private final CancelOrderService cancelOrderService;

    public CancelOrderController(CancelOrderService cancelOrderService) {
        this.cancelOrderService = cancelOrderService;
    }

    @Operation(summary = "取消借閱申請")
    @PostMapping("/{orderId}/cancel")
    public Mono<ResponseEntity<ApiResponse<CancelOrderResponse>>> cancel(
            @PathVariable("orderId") UUID orderId) {
        CancelOrderCommand command = new CancelOrderCommand(orderId);

        return cancelOrderService
                .cancel(command)
                .map(result -> result.match(this::success, this::failure));
    }

    private ResponseEntity<ApiResponse<CancelOrderResponse>> success(CancelOrderResult result) {
        CancelOrderResponse response =
                new CancelOrderResponse(result.orderId(), result.status(), result.closedAt());

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, response));
    }

    private ResponseEntity<ApiResponse<CancelOrderResponse>> failure(String errorCode) {
        HttpStatus status = statusFor(errorCode);

        return ResponseEntity.status(status).body(ApiResponse.failure(status, errorCode));
    }

    private HttpStatus statusFor(String errorCode) {
        return switch (errorCode) {
            case CancelOrderDomainError.NOT_FOUND -> HttpStatus.NOT_FOUND;

            case CancelOrderDomainError.INVALID_STATUS -> HttpStatus.CONFLICT;

            default ->
                    throw new IllegalStateException(
                            "Unexpected cancel order error code: " + errorCode);
        };
    }
}
