package onon1101.lendingsystem.order.borrow;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import onon1101.lendingsystem.order.borrow.error.BorrowOrderDomainError;
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
public class BorrowOrderController {

    private final BorrowOrderService borrowOrderService;

    public BorrowOrderController(BorrowOrderService borrowOrderService) {
        this.borrowOrderService = borrowOrderService;
    }

    @Operation(summary = "確認借出")
    @PostMapping("/{orderId}/borrow")
    public Mono<ResponseEntity<ApiResponse<BorrowOrderResponse>>> borrow(
            @PathVariable("orderId") UUID orderId) {
        BorrowOrderCommand command = new BorrowOrderCommand(orderId);

        return borrowOrderService
                .borrow(command)
                .map(result -> result.match(this::success, this::failure));
    }

    private ResponseEntity<ApiResponse<BorrowOrderResponse>> success(BorrowOrderResult result) {
        BorrowOrderResponse response =
                new BorrowOrderResponse(result.orderId(), result.status(), result.borrowedAt());

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, response));
    }

    private ResponseEntity<ApiResponse<BorrowOrderResponse>> failure(String errorCode) {
        HttpStatus status = statusFor(errorCode);

        return ResponseEntity.status(status).body(ApiResponse.failure(status, errorCode));
    }

    private HttpStatus statusFor(String errorCode) {
        return switch (errorCode) {
            case BorrowOrderDomainError.NOT_FOUND -> HttpStatus.NOT_FOUND;

            case BorrowOrderDomainError.INVALID_STATUS,
                    BorrowOrderDomainError.ITEM_UNAVAILABLE,
                    BorrowOrderDomainError.DUE_AT_EXPIRED ->
                    HttpStatus.CONFLICT;

            default ->
                    throw new IllegalStateException(
                            "Unexpected borrow order error code: " + errorCode);
        };
    }
}
