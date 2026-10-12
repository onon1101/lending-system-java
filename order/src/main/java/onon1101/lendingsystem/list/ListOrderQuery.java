package onon1101.lendingsystem.list;

import java.util.Objects;
import java.util.Set;

public record ListOrderQuery(
        Role role,
        String status,
        int page,
        int size
) {

    private static final Set<String> VALID_STATUSES = Set.of(
            "pending",
            "approved",
            "borrowed",
            "returned",
            "rejected",
            "cancelled"
    );

    public ListOrderQuery {
        Objects.requireNonNull(role, "Role must not be null.");

        if (status != null && !VALID_STATUSES.contains(status)) {
            throw new IllegalArgumentException("Unsupported order status.");
        }

        if (page < 0) {
            throw new IllegalArgumentException("Page number must be greater than or equal to 0.");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Size must be between 1 and 100.");
        }
    }

    public long offset() {
        return (long ) page * size;
    }

    public int fetchLimit() {
        return size + 1;
    }

    public enum Role {
        BORROWER,
        LENDER
    }
}
