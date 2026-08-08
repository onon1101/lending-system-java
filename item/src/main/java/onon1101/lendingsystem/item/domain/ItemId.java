package onon1101.lendingsystem.item.domain;

import java.util.Objects;
import java.util.UUID;

public record ItemId(UUID value) {

    public ItemId {
        Objects.requireNonNull(
                value,
                "Item ID must not be null."
        );
    }

    public static ItemId of(UUID value) {
        return new ItemId(value);
    }
}
