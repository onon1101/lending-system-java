package onon1101.lendingsystem.create;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import onon1101.lendingsystem.create.error.InvalidOrderDatesDomainError;
import onon1101.lendingsystem.create.error.OrderConflictDomainError;
import onon1101.lendingsystem.create.error.OrderItemNotFoundDomainError;
import onon1101.lendingsystem.web.response.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@Tag(name = "order", description = "借閱紀錄相關 API")
@RestController
@RequestMapping("/api/v1/orders")
public class CreateOrderController {

    private final CreateOrderService createOrderService;

    public CreateOrderController(CreateOrderService service) {
        this.createOrderService = service;
    }

    @Operation(summary = "建立借閱申請")
    @PostMapping
    public Mono<ResponseEntity<ApiResponse<CreateOrderResponse>>>
    create(@Valid @RequestBody CreateOrderRequest request) {
        CreateOrderCommand command =
                new CreateOrderCommand(request.itemId(),
                        request.plannedBorrowDate(),
                        request.plannedReturnDate());

        return createOrderService
                .create(command)
                .map(outcome -> outcome.match(this::success, this::failure));
    }

    private ResponseEntity<ApiResponse<CreateOrderResponse>> success(
            CreateOrderResult result
    ) {
        CreateOrderResponse response =
                new CreateOrderResponse(
                        result.orderId(),
                        result
                                .status()
                                .value(),
                        result.plannedBorrowDate(),
                        result.plannedReturnDate()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, response));
    }

    private ResponseEntity<ApiResponse<CreateOrderResponse>> failure(
            String errorCode) {
        HttpStatus status =
                switch (errorCode) {
                    case InvalidOrderDatesDomainError.CODE ->
                            HttpStatus.BAD_REQUEST;
                    case OrderItemNotFoundDomainError.CODE ->
                            HttpStatus.NOT_FOUND;
                    case OrderConflictDomainError.CANNOT_BORROW_OWN_ITEM ->
                            HttpStatus.BAD_REQUEST;
                    case OrderConflictDomainError.ITEM_UNAVAILABLE,
                         OrderConflictDomainError.OPEN_REQUEST_EXISTS ->
                            HttpStatus.CONFLICT;
                    default -> throw new IllegalStateException(
                            "Unexpected create order error code: " + errorCode
                    );
                };

        return ResponseEntity.status(status)
                .body(ApiResponse.failure(status, errorCode));
    }
}
