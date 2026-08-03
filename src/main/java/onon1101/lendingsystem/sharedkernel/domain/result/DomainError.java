package onon1101.lendingsystem.sharedkernel.domain.result;

import java.util.Objects;

public abstract class DomainError {

    protected DomainError(String code, String message) {
        Objects.requireNonNull(code, "Code must not be null.");
        Objects.requireNonNull(message, "Message must not be null.");

        if (code.isBlank()) {
            throw new IllegalArgumentException("code must not be blank.");
        }

        if (message.isBlank()) {
            throw new IllegalArgumentException("message must not be blank.");
        }

        this.code = code;
        this.message = message;
    }

    private String code;
    private String message;

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }
}
// public record DomainError(String code, String message) {
//    public DomainError {
//        Objects.requireNonNull(code, "Code must not be null.");
//        Objects.requireNonNull(message, "Message must not be null.");
//
//        if (code.isBlank()) {
//            throw new IllegalArgumentException("code must not be blank.");
//        }
//
//        if (message.isBlank()) {
//            throw new IllegalArgumentException("message must not be blank.");
//        }
//    }
// }
