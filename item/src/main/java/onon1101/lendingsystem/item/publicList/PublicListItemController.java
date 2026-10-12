package onon1101.lendingsystem.item.publicList;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import onon1101.lendingsystem.web.pagination.PaginationRequest;
import onon1101.lendingsystem.web.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Tag(name = "item", description = "物品相關 API")
@RestController
@RequestMapping("/api/v1/public-items")
public class PublicListItemController {
    private final PublicListItemService publicListItemService;

    public PublicListItemController(PublicListItemService publicListItemService) {
        this.publicListItemService = publicListItemService;
    }

    @Operation(summary = "公開查詢所有物品狀態")
    @GetMapping
    public Mono<ResponseEntity<ApiResponse<PublicListItemResponse>>> listPublic(
            @Valid @ModelAttribute PaginationRequest pagination) {
        var query = new PublicListItemsQuery(pagination.page(), pagination.size());
        return publicListItemService
                .listPublic(query)
                .map(PublicListItemResponse::from)
                .map(PublicListItemController::successfulResponse);
    }

    private static ResponseEntity<ApiResponse<PublicListItemResponse>> successfulResponse(
            PublicListItemResponse response) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, response));
    }
}
