package onon1101.lendingsystem.order.returnOrder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import onon1101.lendingsystem.order.returnOrder.error.ReturnOrderDomainError;
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
public class ReturnOrderController {

    private final ReturnOrderService returnOrderService;

    public ReturnOrderController(ReturnOrderService returnOrderService) {
        this.returnOrderService = returnOrderService;
    }

    @Operation(summary = "確認歸還")
    @PostMapping("/{orderId}/return")
    public Mono<ResponseEntity<ApiResponse<ReturnOrderResponse>>> returnOrder(
            @PathVariable("orderId") UUID orderId) {
        ReturnOrderCommand command = new ReturnOrderCommand(orderId);

        return returnOrderService
                .returnOrder(command)
                .map(result -> result.match(this::success, this::failure));
    }

    private ResponseEntity<ApiResponse<ReturnOrderResponse>> success(ReturnOrderResult result) {
        ReturnOrderResponse response =
                new ReturnOrderResponse(result.orderId(), result.status(), result.returnedAt());

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, response));
    }

    private ResponseEntity<ApiResponse<ReturnOrderResponse>> failure(String errorCode) {
        HttpStatus status = statusFor(errorCode);

        return ResponseEntity.status(status).body(ApiResponse.failure(status, errorCode));
    }

    private HttpStatus statusFor(String errorCode) {
        return switch (errorCode) {
            case ReturnOrderDomainError.NOT_FOUND -> HttpStatus.NOT_FOUND;

            case ReturnOrderDomainError.INVALID_STATUS -> HttpStatus.CONFLICT;

            default ->
                    throw new IllegalStateException(
                            "Unexpected returnOrder order error code: " + errorCode);
        };
    }
}
