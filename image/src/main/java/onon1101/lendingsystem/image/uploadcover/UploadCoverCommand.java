package onon1101.lendingsystem.image.uploadcover;

import java.util.UUID;
import reactor.core.publisher.Mono;

public record UploadCoverCommand(UUID itemId, Mono<ValidatedItemCover> image) {}
