package onon1101.lendingsystem.image.uploadcover;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import onon1101.lendingsystem.image.uploadcover.error.ItemCoverValidationException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class ItemCoverValidator {

    private static final long MAX_PIXELS = 16_000_000;

    public Mono<ValidatedItemCover> validate(byte[] bytes) {
        return Mono.fromCallable(() -> decode(bytes)).subscribeOn(Schedulers.boundedElastic());
    }

    private ValidatedItemCover decode(byte[] bytes) {
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

                return new ValidatedItemCover(bytes, contentType);
            } finally {
                reader.dispose();
            }
        } catch (ItemCoverValidationException exception) {
            throw exception;
        } catch (IOException | RuntimeException exception) {
            throw invalidImage();
        }
    }

    private static ItemCoverValidationException invalidImage() {
        return new ItemCoverValidationException("Item.InvalidCover");
    }
}
