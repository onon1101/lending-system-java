package onon1101.lendingsystem.item.uploadIamge.repository;

import java.util.UUID;
import reactor.core.publisher.Mono;

public interface UploadImageReader {
    Mono<Boolean> isOwnedItem(UUID itemId, long ownerId);
}
