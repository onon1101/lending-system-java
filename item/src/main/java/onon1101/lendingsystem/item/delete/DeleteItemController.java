package onon1101.lendingsystem.item.delete;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import onon1101.lendingsystem.configurations.controller.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "item", description = "物品相關 API")
@RestController
@RequestMapping("/api/v1/item/delete")
public class DeleteItemController {

    private final DeleteItemService deleteItemService;

    public DeleteItemController(
            DeleteItemService deleteItemService) {
        this.deleteItemService = deleteItemService;
    }

    @Operation(summary = "刪除自己的物品")
    @PostMapping()
    public ResponseEntity<ApiResponse<DeleteItemResponse>> delete(
            @Valid @RequestBody DeleteItemRequest request) {
        return deleteItemService
                .delete(new DeleteItemCommand(request.itemId()))
                .match(
                        result ->
                                ResponseEntity.ok(
                                        ApiResponse.success(
                                                HttpStatus.OK,
                                                DeleteItemResponse.from(result))),
                        errorCode ->
                                ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(
                                                ApiResponse.failure(
                                                        HttpStatus.NOT_FOUND,
                                                        errorCode)));
    }
}
