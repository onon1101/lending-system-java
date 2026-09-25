package onon1101.lendingsystem.item.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ItemTests {

    @Test
    void archivedItemCannotBeUpdated() {
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
        Item item =
                Item.create(
                        ItemId.of(UUID.randomUUID()),
                        42L,
                        ItemName.of("Camera"),
                        ItemDescription.of("Mirrorless"),
                        createdAt);

        item.archive(createdAt.plusSeconds(1));

        assertThat(item.availability()).isEqualTo(ItemAvailability.ARCHIVED);
        assertThatThrownBy(
                        () ->
                                item.updateDetails(
                                        ItemName.of("Updated"),
                                        ItemDescription.empty(),
                                        createdAt.plusSeconds(2)))
                .isInstanceOf(ItemDomainException.class);
    }
}
