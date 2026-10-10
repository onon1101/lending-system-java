package onon1101.lendingsystem.image.uploadimage.repository;

import java.util.UUID;
import onon1101.lendingsystem.image.uploadimage.OwnedItemImage;
import reactor.core.publisher.Mono;

public interface UploadItemImageReader {

    Mono<OwnedItemImage> findOwnedItem(UUID itemId, long ownerId);
}
