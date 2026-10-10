package onon1101.lendingsystem.item.uploadImage.repository;

import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

public interface UploadImageWriter {
    Mono<Boolean> replace(
            UUID itemId, long ownerId, String expectedKey, String newKey, Instant updateAt);
}
