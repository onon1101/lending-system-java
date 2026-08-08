package onon1101.lendingsystem.item.delete;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "刪除物品回應")
public record DeleteItemResponse(
        UUID itemId,
        Instant archivedAt
) {

    public static DeleteItemResponse from(DeleteItemResult result) {
        return new DeleteItemResponse(
                result.itemId(),
                result.archivedAt()
        );
    }
}
