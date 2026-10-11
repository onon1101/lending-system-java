package onon1101.lendingsystem.item.uploadCover.repository;

import reactor.core.publisher.Mono;

import java.util.UUID;


public interface UploadCoverReader {

    Mono<OwnedItemCover> findOwnedItem(UUID itemId, long ownerId);
}
