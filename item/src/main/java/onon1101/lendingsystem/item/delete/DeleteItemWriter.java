package onon1101.lendingsystem.item.delete;

import java.time.Instant;
import java.util.UUID;

public interface DeleteItemWriter {

    boolean archiveOwnedItem(
            UUID itemId,
            long ownerId,
            Instant archivedAt
    );
}
