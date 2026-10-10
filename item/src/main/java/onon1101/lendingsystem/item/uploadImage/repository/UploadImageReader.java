package onon1101.lendingsystem.item.uploadImage.repository;

import reactor.core.publisher.Mono;

import java.util.UUID;


public interface UploadImageReader {

    Mono<OwnedItemImage> findOwnedItem(UUID itemId, long ownerId);
}
