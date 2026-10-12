package onon1101.lendingsystem.order.list;

import java.util.Objects;
import java.util.Set;
import onon1101.lendingsystem.shared.pagination.PageSpec;

public record ListOrderQuery(
        Role role,
        String status,
        PageSpec pagination
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

        Objects.requireNonNull(pagination, "Pagination must not be null.");
    }

    public enum Role {
        BORROWER,
        LENDER
    }
}
