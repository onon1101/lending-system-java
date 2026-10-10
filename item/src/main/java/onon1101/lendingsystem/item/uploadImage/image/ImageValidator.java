package onon1101.lendingsystem.item.uploadImage.image;

import onon1101.lendingsystem.item.uploadImage.ValidatedImage;

import onon1101.lendingsystem.item.uploadImage.exception.UploadImageValidationException;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;

@Component
public class ImageValidator {

    private static final long MAX_PIXELS = 16_000_000;

    public Mono<ValidatedImage> validate(
            byte[] bytes
    ) {
        return Mono
                .fromCallable(() -> decode(bytes))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private ValidatedImage decode(byte[] bytes) {

        if (bytes.length == 0) {
            throw invalidImage();
        }

        try (var input = new MemoryCacheImageInputStream(new ByteArrayInputStream(bytes))) {
            var readers = ImageIO.getImageReaders(input);

            if (!readers.hasNext()) {
                throw invalidImage();
            }

            var reader = readers.next();

            try {
                reader.setInput(input, true, true);

                String contentType =
                        switch (reader.getFormatName().toLowerCase(Locale.ROOT)) {
                            case "jpeg", "jpg" -> "image/jpeg";
                            case "png" -> "image/png";
                            case "webp" -> "image/webp";
                            default -> throw invalidImage();
                        };

                int width = reader.getWidth(0);
                int height = reader.getHeight(0);

                if (width <= 0 || height <= 0 || (long) width * height > MAX_PIXELS) {
                    throw invalidImage();
                }

                var decoded = reader.read(0);

                if (decoded == null) {
                    throw invalidImage();
                }

                decoded.flush();

                return new ValidatedImage(bytes, contentType);
            } finally {
                reader.dispose();
            }
        } catch (UploadImageValidationException exception) {
            throw exception;
        } catch (IOException | RuntimeException exception) {
            throw invalidImage();
        }
    }

    private static UploadImageValidationException invalidImage() {
        return new UploadImageValidationException("Item.InvalidImage");
    }
}
