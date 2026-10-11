package onon1101.lendingsystem.item.uploadCover;

import java.util.UUID;
import reactor.core.publisher.Mono;

public record UploadCoverCommand(UUID itemId, Mono<ValidatedImage> image) {}
