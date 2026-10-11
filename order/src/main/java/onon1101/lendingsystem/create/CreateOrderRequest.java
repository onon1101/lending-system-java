package onon1101.lendingsystem.create;

import io.swagger.v3.oas.annotations.media.Schema;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "建立借閱申請")
public record CreateOrderRequest (
        @NotNull
        @Schema(
                description = "物品公開 ID",
                example = "c9cbb602-466c-4cec-93b9-5dd50be9b2bf"
        )
        UUID itemId,

        @NotNull
        @Schema(
                description = "預計借閱日期",
                example = "2026-10-25"
        )
        LocalDate plannedBorrowDate,

        @NotNull
        @Schema(
                description = "預計歸還日期",
                example = "2026-10-20"
        )
        LocalDate plannedReturnDate
){
}
