package onon1101.lendingsystem.image.uploadimage;

import java.util.UUID;
import reactor.core.publisher.Mono;

public record UploadItemImageCommand(UUID itemId, Mono<ValidatedItemImage> image) {}
