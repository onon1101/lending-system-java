package onon1101.lendingsystem.item.retrieve;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import onon1101.lendingsystem.configurations.controller.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "item", description = "物品相關 API")
@RestController
@RequestMapping("/api/v1/item/retrieve")
public class RetrieveItemController {

    private final RetrieveItemService retrieveItemService;

    public RetrieveItemController(RetrieveItemService retrieveItemService) {
        this.retrieveItemService = retrieveItemService;
    }

    @Operation(summary = "取得指定物品")
    @GetMapping("/{itemId}")
    public ResponseEntity<ApiResponse<RetrieveItemResponse>> retrieve(
            @PathVariable UUID itemId
    ) {
        return retrieveItemService
                .retrieve(itemId)
                .match(
                        result ->
                                ResponseEntity.ok(
                                        ApiResponse.success(
                                                HttpStatus.OK,
                                                RetrieveItemResponse.from(
                                                        result)
                                        )
                                ),
                        errorCode ->
                                ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(ApiResponse.failure(
                                                HttpStatus.NOT_FOUND,
                                                errorCode
                                        ))
                );
    }
}
