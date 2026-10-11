package onon1101.lendingsystem.item.uploadCover;

import java.util.UUID;
import onon1101.lendingsystem.infrastructure.image.ValidatedImage;
import reactor.core.publisher.Mono;

public record UploadCoverCommand(UUID itemId, Mono<ValidatedImage> image) {}
