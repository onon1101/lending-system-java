package onon1101.lendingsystem.user.register;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "註冊回應")
public record RegisterResponse(
        @Schema(description = "使用者公開 ID", example = "019fdad1-d6b2-7930-ae47-0525bb583a8e")
                UUID userId) {}
