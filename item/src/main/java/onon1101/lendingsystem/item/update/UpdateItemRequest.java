package onon1101.lendingsystem.item.update;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Schema(description = "更新物品請求")
public record UpdateItemRequest(
        @Schema(description = "物品編號")
        @NotBlank
        UUID itemId,

        @Schema(description = "物品名稱")
        @NotBlank
        @Size(max = 100)
        String name,

        @Schema(description = "物品描述")
        @Size(max = 2000)
        String description
) {
}
