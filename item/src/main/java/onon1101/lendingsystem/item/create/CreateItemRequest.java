package onon1101.lendingsystem.item.create;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "建立物品請求")
public record CreateItemRequest(

    @Schema(description = "物品名稱")
    String name,

    @Schema(description = "物品描述")
    String description
){}
