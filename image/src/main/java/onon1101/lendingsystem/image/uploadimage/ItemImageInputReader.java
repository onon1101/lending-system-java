package onon1101.lendingsystem.image.uploadimage;

import onon1101.lendingsystem.image.uploadimage.error.ItemImageValidationException;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ItemImageInputReader {

    private static final int MAX_BYTES = 5 * 1024 * 1024;

    private final ItemImageValidator validator;

    public ItemImageInputReader(ItemImageValidator validator) {
        this.validator = validator;
    }

    public Mono<ValidatedItemImage> read(FilePart file) {
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
                .switchIfEmpty(Mono.error(new ItemImageValidationException("Item.InvalidImage")))
                .onErrorMap(
                        DataBufferLimitException.class,
                        error -> new ItemImageValidationException("Item.ImageTooLarge"))
                .flatMap(validator::validate);
    }
}
