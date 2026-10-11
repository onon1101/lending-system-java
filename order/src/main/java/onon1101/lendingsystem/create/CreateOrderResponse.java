package onon1101.lendingsystem.create;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "建立借閱申請結果")
public record CreateOrderResponse(
        @Schema(description = "借閱紀錄公開 ID")
        UUID orderId,

        @Schema(description = "借閱紀錄狀態", example = "pending")
        String status,

        @Schema(description = "預計借閱日期", example = "2026-10-15")
        LocalDate plannedBorrowDate,

        @Schema(description = "預計歸還日期", example = "2026-10-25")
        LocalDate plannedReturnDate
) {
}
