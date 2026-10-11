package onon1101.lendingsystem.item.uploadCover.repository;

import java.util.UUID;
import reactor.core.publisher.Mono;

public interface UploadCoverReader {

    Mono<OwnedItemCover> findOwnedItem(UUID itemId, long ownerId);
}
