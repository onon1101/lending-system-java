package onon1101.lendingsystem.item.uploadImage;

import java.util.UUID;
import reactor.core.publisher.Mono;

public record UploadImageCommand(UUID itemId, Mono<ValidatedImage> image) {}
