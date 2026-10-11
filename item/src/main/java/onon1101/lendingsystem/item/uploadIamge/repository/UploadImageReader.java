package onon1101.lendingsystem.item.uploadIamge.repository;

import reactor.core.publisher.Mono;
import java.util.UUID;

public interface UploadImageReader {
    Mono<Boolean> isOwnedItem(UUID itemId, long ownerId);
}
