package onon1101.lendingsystem.item.create;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.net.URI;
import onon1101.lendingsystem.configurations.controller.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "item", description = "物品相關 API")
@Validated
@RestController
@RequestMapping("/api/v1/items/create")
public class CreateItemController {
  TEIMSTAMPE
  private final CreateItemService createItemService;

  public CreateItemController(CreateItemService createItemService) {
    this.createItemService = createItemService;
  }

  @Operation(summary = "建立物品")
  @PostMapping
  public ResponseEntity<ApiResponse<CreateItemResponse>>
  create(@RequestHeader("Idempotency-Key") @NotBlank @Size(max = 128)
         @Pattern(regexp = "^[A-Za-z0-9._:-]+$") String idempotencyKey,
         @Valid @RequestBody CreateItemRequest request) {
    CreateItemResult result = createItemService.create(new CreateItemCommand(
        idempotencyKey, request.name(), request.description()));

    CreateItemResponse response = new CreateItemResponse(result.itemId());

    URI location = URI.create("/api/v1/items/" + result.itemId());

    return ResponseEntity.created(location).body(
        ApiResponse.success(HttpStatus.CREATED, response));
  }
}
