package onon1101.lendingsystem.item.uploadIamge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import onon1101.lendingsystem.application.currentuser.CurrentUserContext;
import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;
import onon1101.lendingsystem.infrastructure.image.ImageReader;
import onon1101.lendingsystem.infrastructure.image.ImageStorage;
import onon1101.lendingsystem.infrastructure.image.ImageValidationException;
import onon1101.lendingsystem.infrastructure.image.ValidatedImage;
import onon1101.lendingsystem.item.uploadIamge.repository.SavedImage;
import onon1101.lendingsystem.item.uploadIamge.repository.UploadImageReader;
import onon1101.lendingsystem.item.uploadIamge.repository.UploadImageWriter;
import onon1101.lendingsystem.shared.time.IClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

class UploadImageServiceTests {
    private final UUID itemId = UUID.randomUUID();
    private final Instant now = Instant.parse("2026-10-11T00:00:00Z");
    private final CurrentUserProvider users = mock(CurrentUserProvider.class);
    private final UploadImageReader itemReader = mock(UploadImageReader.class);
    private final UploadImageWriter writer = mock(UploadImageWriter.class);
    private final ImageReader reader = mock(ImageReader.class);
    private final ImageStorage storage = mock(ImageStorage.class);
    private final IClock clock = mock(IClock.class);
    private final FilePart file = mock(FilePart.class);
    private final byte[] bytes = {1, 2};
    private final SingleImageUploader singleImageUploader =
            new SingleImageUploader(reader, storage, writer, clock);
    private final UploadImageService service =
            new UploadImageService(users, itemReader, singleImageUploader);

    @BeforeEach
    void setup() {
        when(users.getCurrentUser())
                .thenReturn(
                        Mono.just(
                                new CurrentUserContext(
                                        7L,
                                        UUID.randomUUID(),
                                        "owner",
                                        "owner@example.com",
                                        "active")));
    }

    private MultiValueMap<String, Part> form(FilePart... files) {
        var form = new LinkedMultiValueMap<String, Part>();
        for (var image : files) {
            form.add("files", image);
        }
        return form;
    }

    private UploadImageResponse upload(FilePart... files) {
        var result =
                service.uploadImage(itemId, Mono.just(form(files))).block(Duration.ofSeconds(5));
        return result.match(
                response -> response,
                code -> {
                    throw new AssertionError(code);
                });
    }

    private void prepareUpload() {
        when(itemReader.isOwnedItem(itemId, 7L)).thenReturn(Mono.just(true));
        when(reader.read(file)).thenReturn(Mono.just(new ValidatedImage(bytes, "image/png")));
        when(storage.upload(itemId, bytes, "image/png")).thenReturn(Mono.just("new-key"));
        when(storage.previewUrl("new-key")).thenReturn(Mono.just("https://example.com/image"));
        when(clock.now()).thenReturn(now);
    }

    @Test
    void missingOwnedItemDoesNotSubscribeToMultipartBody() {
        when(itemReader.isOwnedItem(itemId, 7L)).thenReturn(Mono.just(false));
        var subscribed = new AtomicBoolean();
        var parts =
                Mono.fromSupplier(
                        () -> {
                            subscribed.set(true);
                            return form(file);
                        });
        var result = service.uploadImage(itemId, parts).block(Duration.ofSeconds(5));
        assertEquals("Item.NotFound", result.match(value -> "success", code -> code));
        assertFalse(subscribed.get());
        verifyNoInteractions(reader, storage, writer);
    }

    @Test
    void invalidImageDoesNotPreventLaterImageFromUploading() {
        prepareUpload();
        var invalid = mock(FilePart.class);
        var imageId = UUID.randomUUID();
        when(reader.read(invalid))
                .thenReturn(Mono.error(new ImageValidationException("Item.CoverTooLarge")));
        when(writer.append(itemId, 7L, "new-key", "image/png", now))
                .thenReturn(Mono.just(new SavedImage(imageId, 1)));

        var response = upload(invalid, file);
        assertEquals(2, response.images().size());
        assertEquals("Item.ImageTooLarge", response.images().get(0).errorCode());
        assertEquals(1, response.images().get(1).index());
        assertEquals(imageId, response.images().get(1).imageId());
        assertNull(response.images().get(1).errorCode());
        verify(storage, never()).delete(any());
    }

    @Test
    void previewFailureCleansUploadedObjectBeforeDatabaseWrite() {
        prepareUpload();
        when(storage.previewUrl("new-key"))
                .thenReturn(Mono.error(new IllegalStateException("preview unavailable")));
        when(storage.delete("new-key")).thenReturn(Mono.empty());

        var response = upload(file);
        assertEquals("Item.ImageUploadFailed", response.images().get(0).errorCode());
        verify(storage).delete("new-key");
        verifyNoInteractions(writer);
    }

    @Test
    void unknownDatabaseOutcomeRetainsUploadedObject() {
        prepareUpload();
        when(writer.append(itemId, 7L, "new-key", "image/png", now))
                .thenReturn(Mono.error(new IllegalStateException("commit outcome unknown")));

        var response = upload(file);
        assertEquals("Item.ImageSaveUncertain", response.images().get(0).errorCode());
        verify(storage, never()).delete(any());
    }

    @Test
    void ownershipLostBeforeAppendCleansUploadedObject() {
        prepareUpload();
        when(writer.append(itemId, 7L, "new-key", "image/png", now)).thenReturn(Mono.empty());
        when(storage.delete("new-key")).thenReturn(Mono.empty());

        var response = upload(file);
        assertEquals("Item.NotFound", response.images().get(0).errorCode());
        verify(storage).delete("new-key");
    }
}
