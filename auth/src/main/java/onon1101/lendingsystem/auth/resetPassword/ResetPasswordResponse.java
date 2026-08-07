package onon1101.lendingsystem.auth.resetPassword;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "重設密碼回應")
public record ResetPasswordResponse(
        @Schema(description = "處理結果訊息", example = "已修改密碼") String message) {}
