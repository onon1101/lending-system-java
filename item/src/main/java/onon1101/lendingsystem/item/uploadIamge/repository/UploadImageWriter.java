package onon1101.lendingsystem.item.uploadIamge.repository;

import java.time.Instant;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface UploadImageWriter {
    Mono<SavedImage> append(
            UUID itemId, long ownerId, String objectKey, String contentType, Instant now);
}
