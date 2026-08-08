package onon1101.lendingsystem.user.whoami;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "目前登入使用者資料")
public record WhoAmIResponse(
        @Schema(description = "使用者公開識別碼")
        UUID userId,

        @Schema(description = "使用者名稱")
        String username,

        @Schema(description = "電子郵件")
        String email,

        @Schema(description = "帳號狀態")
        String status
) {
}
