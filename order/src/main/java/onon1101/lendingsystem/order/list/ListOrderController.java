package onon1101.lendingsystem.order.list;
import onon1101.lendingsystem.shared.pagination.PageSpec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

import onon1101.lendingsystem.web.pagination.PaginationRequest;
import onon1101.lendingsystem.web.response.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

import java.util.Locale;

@Tag(name = "order", description = "借閱紀錄相關 API")
@RestController
@RequestMapping("/api/v1/orders")
public class ListOrderController {

    private final ListOrderService listOrderService;

    public ListOrderController(ListOrderService listOrderService) {
        this.listOrderService = listOrderService;
    }

    @Operation(summary = "查詢我的借入或借出訂單")
    @GetMapping
    public Mono<ResponseEntity<ApiResponse<ListOrderResponse>>> list(
            @RequestParam(name = "role", defaultValue = "borrower")
            @Pattern(regexp = "borrower|lender")
            String role,

            @RequestParam(name = "status", required = false)
            @Pattern(
                    regexp = "pending|approved|borrowed|returned|rejected" +
                            "|cancelled"
            )
            String status,

            @Valid
            @ModelAttribute
            PaginationRequest pagination
    ) {
        ListOrderQuery query = new ListOrderQuery(
                ListOrderQuery.Role.valueOf(role.toUpperCase(Locale.ROOT)),
                status,
                new PageSpec(pagination.page(), pagination.size()));

        return listOrderService
                .list(query)
                .map(ListOrderResponse::from)
                .map(ListOrderController::successfulResponse);
    }

    private static ResponseEntity<ApiResponse<ListOrderResponse>>
    successfulResponse(ListOrderResponse response) {
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK, response)
        );
    }
}
