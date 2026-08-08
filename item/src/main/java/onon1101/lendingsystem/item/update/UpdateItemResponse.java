package onon1101.lendingsystem.item.update;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "更新物品回應")
public record UpdateItemResponse(
        @Schema(description = "物品公開 API")
        UUID itemId,

        @Schema(description = "物品名稱")
        String name,

        @Schema(description = "物品描述")
        String description,

        @Schema(description = "最後更新時間")
        Instant updatedAt
) {
}
