package onon1101.lendingsystem.item.create;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "建立物品回應")
public record CreateItemResponse(
        @Schema(description = "物品公開 ID")
        UUID itemId
) {
}
