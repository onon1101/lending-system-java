package onon1101.lendingsystem.item.list;

import reactor.core.publisher.Mono;

public interface ItemPreviewUrlReader {
    Mono<String> resolve(String objectKey);
}
