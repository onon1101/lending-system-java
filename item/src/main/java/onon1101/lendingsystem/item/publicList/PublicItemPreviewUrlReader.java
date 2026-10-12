package onon1101.lendingsystem.item.publicList;

import reactor.core.publisher.Mono;

public interface PublicItemPreviewUrlReader {

    Mono<String> resolve(String objectKey);
}
