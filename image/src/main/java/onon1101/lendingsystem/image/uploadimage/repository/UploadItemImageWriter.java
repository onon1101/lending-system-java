package onon1101.lendingsystem.image.uploadimage.repository;

import java.time.Instant;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface UploadItemImageWriter {

    Mono<Boolean> replace(
            UUID itemId, long ownerId, String expectedKey, String newKey, Instant updateAt);
}
