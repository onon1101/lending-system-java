package onon1101.lendingsystem.item.uploadIamge.repository;

import reactor.core.publisher.Mono;
import java.time.Instant;
import java.util.UUID;

public interface UploadImageWriter {
    Mono<SavedImage> append(UUID itemId, long ownerId, String objectKey,
                            String contentType, Instant now);
}
