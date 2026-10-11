package onon1101.lendingsystem.infrastructure.image;

import onon1101.lendingsystem.item.uploadCover.ValidatedImage;

import onon1101.lendingsystem.item.uploadCover.exception.UploadCoverValidationException;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;


@Component
public class ImageReader {

    private static final int MAX_BYTES = 5 * 1024 * 1024;

    private final ImageValidator validator;

    public ImageReader(ImageValidator validator) {
        this.validator = validator;
    }

    private static Mono<DataBuffer> convert2DataBuffer(FilePart file) {
        return DataBufferUtils.join(file.content(), MAX_BYTES);
    }

    private static Mono<byte[]> invalidImage() {
        return Mono.error(
                new UploadCoverValidationException("Item.InvalidCover"));
    }

    private static byte[] readImage(DataBuffer buffer) {
        try {
            byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
            return bytes;
        } finally {
            DataBufferUtils.release(buffer);
        }
    }

    public Mono<ValidatedImage> read(FilePart file) {
        return Mono
                .defer(() -> convert2DataBuffer(file))
                .map(ImageReader::readImage)
                .switchIfEmpty(invalidImage())
                .onErrorMap(
                        DataBufferLimitException.class,
                        error -> new UploadCoverValidationException(
                                "Item.CoverTooLarge"))
                .flatMap(validator::validate);
    }
}
