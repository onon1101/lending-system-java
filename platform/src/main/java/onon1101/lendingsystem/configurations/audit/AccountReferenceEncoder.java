package onon1101.lendingsystem.configurations.audit;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccountReferenceEncoder {

    private final SecretKeySpec secretKey;

    public AccountReferenceEncoder(@Value("${lending.audit.hmac-key}") String secret) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException(
                    "Audit HMAC key must contain at least 32 characters");
        }
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    public String encode(String normalizedUsername) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);

            byte[] result = mac.doFinal(normalizedUsername.getBytes(StandardCharsets.UTF_8));

            // 縮短成適合搜尋的代碼
            return HexFormat.of().formatHex(result, 0, 8);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not create account reference", exception);
        }
    }
}
