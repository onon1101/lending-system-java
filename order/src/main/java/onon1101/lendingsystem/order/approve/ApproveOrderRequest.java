package onon1101.lendingsystem.order.approve;

import io.swagger.v3.oas.annotations.media.Schema;

import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

public record ApproveOrderRequest(
        @NotNull
        @Schema(
                description = "約定歸還期限。",
                example = "2026-10-25T18:00:00+08:00"
        )

        OffsetDateTime dueAt
) {
}
