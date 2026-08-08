package onon1101.lendingsystem.item.retrieve;

import java.util.Optional;
import java.util.UUID;

public interface RetrieveItemReader {

    Optional<RetrievedItem> findVisibleItem(
            UUID itemId,
            long viewId
    );
}
