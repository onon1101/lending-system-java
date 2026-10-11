package onon1101.lendingsystem.image.uploadcover;

import onon1101.lendingsystem.image.uploadcover.error.ItemCoverValidationException;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ItemCoverInputReader {

    private static final int MAX_BYTES = 5 * 1024 * 1024;

    private final ItemCoverValidator validator;

    public ItemCoverInputReader(ItemCoverValidator validator) {
        this.validator = validator;
    }

    public Mono<ValidatedItemCover> read(FilePart file) {
        return Mono.defer(() -> DataBufferUtils.join(file.content(), MAX_BYTES))
                .map(
                        buffer -> {
                            try {
                                byte[] bytes = new byte[buffer.readableByteCount()];
                                buffer.read(bytes);
                                return bytes;
                            } finally {
                                DataBufferUtils.release(buffer);
                            }
                        })
                .switchIfEmpty(Mono.error(new ItemCoverValidationException("Item.InvalidCover")))
                .onErrorMap(
                        DataBufferLimitException.class,
                        error -> new ItemCoverValidationException("Item.CoverTooLarge"))
                .flatMap(validator::validate);
    }
}
