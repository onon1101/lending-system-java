package onon1101.lendingsystem.sharedkernel.domain.result;

import java.util.Objects;

public record DomainError(String code, String message) {
    public DomainError {
        Objects.requireNonNull(code, "Code must not be null.");
        Objects.requireNonNull(message, "Message must not be null.");

        if (code.isBlank()) {
            throw new IllegalArgumentException("code must not be blank.");
        }

        if (message.isBlank()) {
            throw new IllegalArgumentException("message must not be blank.");
        }
    }
}
