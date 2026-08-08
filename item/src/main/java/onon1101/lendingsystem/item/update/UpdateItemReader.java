package onon1101.lendingsystem.item.update;

import onon1101.lendingsystem.item.domain.Item;

import java.util.Optional;
import java.util.UUID;

public interface UpdateItemReader {

    Optional<Item> finOwnedItem(UUID itemId, long ownerId);
}
