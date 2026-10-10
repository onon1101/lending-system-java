package onon1101.lendingsystem.item.uploadImage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import javax.imageio.ImageIO;
import onon1101.lendingsystem.item.uploadImage.exception.UploadImageValidationException;
import onon1101.lendingsystem.item.uploadImage.image.ImageReader;
import onon1101.lendingsystem.item.uploadImage.image.ImageValidator;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;

class UploadImageInputTests {
    private final ImageReader reader = new ImageReader(new ImageValidator());

    private FilePart file(byte[] bytes) {
        var file = mock(FilePart.class);
        when(file.content()).thenReturn(Flux.just(DefaultDataBufferFactory.sharedInstance.wrap(bytes)));
        return file;
    }

    @Test
    void acceptsDecodedPngAndJpeg() throws Exception {
        for (String format : new String[]{"png", "jpeg"}) {
            var output = new ByteArrayOutputStream();
            ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), format, output);
            byte[] bytes = output.toByteArray();
            var image = reader.read(file(bytes)).block(Duration.ofSeconds(5));
            assertEquals("image/" + format, image.contentType());
            assertArrayEquals(bytes, image.bytes());
        }
    }

    @Test
    void rejectsEmptyAndFakeImageContent() {
        for (byte[] bytes : new byte[][]{new byte[0], {1, 2, 3}}) {
            var error = assertThrows(UploadImageValidationException.class,
                    () -> reader.read(file(bytes)).block(Duration.ofSeconds(5)));
            assertEquals("Item.InvalidImage", error.errorCode());
        }
    }

    @Test
    void rejectsMissingContent() {
        var file = mock(FilePart.class);
        when(file.content()).thenReturn(Flux.empty());
        var error = assertThrows(UploadImageValidationException.class,
                () -> reader.read(file).block(Duration.ofSeconds(5)));
        assertEquals("Item.InvalidImage", error.errorCode());
    }

    @Test
    void mapsOversizeContentToImageTooLarge() {
        var error = assertThrows(UploadImageValidationException.class,
                () -> reader.read(file(new byte[5 * 1024 * 1024 + 1])).block(Duration.ofSeconds(5)));
        assertEquals("Item.ImageTooLarge", error.errorCode());
    }

    @Test
    void webpDecoderIsAvailable() {
        assertTrue(ImageIO.getImageReadersByFormatName("webp").hasNext());
    }
}
