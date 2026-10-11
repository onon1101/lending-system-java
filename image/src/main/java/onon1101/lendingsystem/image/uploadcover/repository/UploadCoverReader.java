package onon1101.lendingsystem.image.uploadcover.repository;

import java.util.UUID;
import onon1101.lendingsystem.image.uploadcover.OwnedItemCover;
import reactor.core.publisher.Mono;

public interface UploadCoverReader {

    Mono<OwnedItemCover> findOwnedItem(UUID itemId, long ownerId);
}
