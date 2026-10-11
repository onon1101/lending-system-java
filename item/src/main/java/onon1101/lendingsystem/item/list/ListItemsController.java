package onon1101.lendingsystem.item.list;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import onon1101.lendingsystem.web.response.ApiResponse;

import onon1101.lendingsystem.web.pagination.PaginationRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@Tag(name = "item", description = "物品相關 API")
@RestController
@RequestMapping("/api/v1/items")
public class ListItemsController {

    private final ListItemsService listItemsService;

    public ListItemsController(
            ListItemsService listItemsService
    ) {
        this.listItemsService = listItemsService;
    }

    @Operation(summary = "查詢可察看的物品列表")
    @GetMapping
    public Mono<ResponseEntity<ApiResponse<ListItemResponse>>> list(
            @Valid
            @ModelAttribute
            PaginationRequest pagination
    ) {
        return listItemsService
                .list(pagination)
                .map(ListItemsController::successfulResponse);
    }

    private static ResponseEntity<ApiResponse<ListItemResponse>> successfulResponse(ListItemResponse response) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, response));
    }
}
