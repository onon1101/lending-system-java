package onon1101.lendingsystem.order.approve;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import onon1101.lendingsystem.order.approve.error.ApproveOrderDomainError;
import onon1101.lendingsystem.web.response.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

import java.util.UUID;

@Tag(name = "order", description = "借閱紀錄相關 API")
@RestController
@RequestMapping("/api/v1/orders")
public class ApproveOrderController {

    private final ApproveOrderService approveOrderService;

    public ApproveOrderController(ApproveOrderService approveOrderService) {
        this.approveOrderService = approveOrderService;
    }

    @Operation(summary = "核准借閱申請")
    @PostMapping("/{orderId}/approve")
    public Mono<ResponseEntity<ApiResponse<ApproveOrderResponse>>> approve(
            @PathVariable("orderId") UUID orderId,
            @Valid @RequestBody ApproveOrderRequest request
    ) {
        ApproveOrderCommand command = new ApproveOrderCommand(
                orderId,
                request
                        .dueAt()
                        .toInstant()
        );

        return approveOrderService
                .approve(command)
                .map(result -> result.match(this::success, this::failure));
    }

    private ResponseEntity<ApiResponse<ApproveOrderResponse>> success(
            ApproveOrderResult result) {
        ApproveOrderResponse response = new ApproveOrderResponse(
                result.orderId(),
                result.status(),
                result.approvedAt(),
                result.dueAt()
        );

        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK, response)
        );
    }

    private ResponseEntity<ApiResponse<ApproveOrderResponse>> failure(
            String errorCode
    ) {
        HttpStatus status = statusFor(errorCode);

        return ResponseEntity
                .status(status)
                .body(ApiResponse.failure(status, errorCode));
    }

    private HttpStatus statusFor(String errorCode) {
        return switch (errorCode) {
            case ApproveOrderDomainError.NOT_FOUND -> HttpStatus.NOT_FOUND;

            case ApproveOrderDomainError.INVALID_DUE_AT ->
                    HttpStatus.BAD_REQUEST;

            case ApproveOrderDomainError.INVALID_STATUS,
                 ApproveOrderDomainError.ITEM_UNAVAILABLE,
                 ApproveOrderDomainError.ACTIVE_ORDER_EXISTS ->
                    HttpStatus.CONFLICT;

            default -> throw new IllegalStateException(
                    "Unexpected approve order error code: " + errorCode);
        };
    }
}
