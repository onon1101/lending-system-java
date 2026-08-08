package onon1101.lendingsystem.item.update;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import onon1101.lendingsystem.configurations.controller.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "item", description = "物品相關 API")
@RestController
@RequestMapping("/api/v1/items/update")
public class UpdateItemController {

    private final UpdateItemService updateItemService;

    public UpdateItemController(UpdateItemService updateItemService) {
        this.updateItemService = updateItemService;
    }

    @Operation(summary = "更新自己的物品")
    @PostMapping()
    public ResponseEntity<ApiResponse<UpdateItemResponse>> update(
            @Valid @RequestBody UpdateItemRequest request
    ) {
        return updateItemService
                .update(
                        new UpdateItemCommand(
                                request.itemId(),
                                request.name(),
                                request.description()
                        )
                )
                .match(
                        result -> {
                            UpdateItemResponse response =
                                    new UpdateItemResponse(
                                            result.itemId(),
                                            result.name(),
                                            result.description(),
                                            result.updatedAt()
                                    );

                            return ResponseEntity.ok(
                                    ApiResponse.success(
                                            HttpStatus.OK,
                                            response
                                    )
                            );
                        },
                        errorCode ->
                                ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(ApiResponse.failure(
                                                HttpStatus.NOT_FOUND,
                                                errorCode
                                        ))
                );
    }
}
