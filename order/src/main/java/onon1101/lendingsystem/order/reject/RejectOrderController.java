package onon1101.lendingsystem.order.reject;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import onon1101.lendingsystem.order.reject.error.RejectOrderDomainError;
import onon1101.lendingsystem.web.response.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

import java.util.UUID;

@Tag(name = "order", description = "借閱紀錄相關 API")
@RestController
@RequestMapping("/api/v1/orders")
public class RejectOrderController {

    private final RejectOrderService rejectOrderService;

    public RejectOrderController(
            RejectOrderService rejectOrderService
    ) {
        this.rejectOrderService = rejectOrderService;
    }

    @Operation(summary = "拒絕借閱申請")
    @PostMapping("/{orderId}/reject")
    public Mono<ResponseEntity<ApiResponse<RejectOrderResponse>>> reject(
            @PathVariable("orderId") UUID orderId
    ) {
        RejectOrderCommand command = new RejectOrderCommand(orderId);

        return rejectOrderService.reject(command)
                .map(result ->
                        result.match(this::success, this::failure)
                );
    }

    private ResponseEntity<ApiResponse<RejectOrderResponse>> success(
            RejectOrderResult result
    ) {
        RejectOrderResponse response = new RejectOrderResponse(
                result.orderId(),
                result.status(),
                result.closedAt()
        );

        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK, response)
        );
    }

    private ResponseEntity<ApiResponse<RejectOrderResponse>> failure(
            String errorCode
    ) {
        HttpStatus status = statusFor(errorCode);

        return ResponseEntity.status(status)
                .body(ApiResponse.failure(status, errorCode));
    }

    private HttpStatus statusFor(String errorCode) {
        return switch (errorCode) {
            case RejectOrderDomainError.NOT_FOUND ->
                    HttpStatus.NOT_FOUND;

            case RejectOrderDomainError.INVALID_STATUS ->
                    HttpStatus.CONFLICT;

            default -> throw new IllegalStateException(
                    "Unexpected reject order error code: " + errorCode
            );
        };
    }
}
