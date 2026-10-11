package onon1101.lendingsystem.item.uploadCover.repository;

import java.time.Instant;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface UploadCoverWriter {
    Mono<Boolean> replace(
            UUID itemId, long ownerId, String expectedKey, String newKey, Instant updateAt);
}
