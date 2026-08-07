package onon1101.lendingsystem.sharedkernel.domain.result;

import java.util.Objects;

/**
 * 業務錯誤的基底類別
 */
public abstract class DomainError {
    /**
     * 錯誤代碼
     */
    private final String code;

    /**
     * 錯誤詳細訊息
     */
    private final String message;

    /**
     * 建構子
     * @param code 錯誤代碼
     * @param message 錯誤詳細訊息
     */
    protected DomainError(
            String code,
            String message
    ) {
        if (code.isBlank()) {
            throw new IllegalArgumentException("code must not be blank.");
        }

        if (message.isBlank()) {
            throw new IllegalArgumentException("message must not be blank.");
        }

        this.code = code;
        this.message = message;
    }

    /**
     * 取得錯誤代碼
     * @return 代碼
     */
    public final String code() {
        return code;
    }

    /**
     * 錯誤訊息
     * @return 訊息
     */
    public final String message() {
        return message;
    }
}
