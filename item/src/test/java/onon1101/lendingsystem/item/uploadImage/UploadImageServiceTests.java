package onon1101.lendingsystem.item.uploadImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import onon1101.lendingsystem.item.uploadImage.repository.OwnedItemImage;
import java.util.concurrent.atomic.AtomicBoolean;
import onon1101.lendingsystem.application.currentuser.CurrentUserContext;
import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;
import onon1101.lendingsystem.infrastructure.image.ImageStorage;
import onon1101.lendingsystem.shared.time.IClock;
import onon1101.lendingsystem.item.uploadImage.repository.UploadImageReader;
import onon1101.lendingsystem.item.uploadImage.repository.UploadImageWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

class UploadImageServiceTests {
    private final UUID itemId = UUID.randomUUID();
    private final Instant now = Instant.parse("2026-10-10T00:00:00Z");
    private final UploadImageReader reader = mock(UploadImageReader.class);
    private final UploadImageWriter writer = mock(UploadImageWriter.class);
    private final CurrentUserProvider users = mock(CurrentUserProvider.class);
    private final ImageStorage storage = mock(ImageStorage.class);
    private final IClock clock = mock(IClock.class);
    private final UploadImageService service =
            new UploadImageService(reader, writer, users, storage, clock);
    private final byte[] bytes = {1, 2};

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

    private UploadImageCommand command() {
        return new UploadImageCommand(
                itemId, Mono.just(new ValidatedImage(bytes, "image/png")));
    }

    private void prepareUpload() {
        when(reader.findOwnedItem(itemId, 7L)).thenReturn(Mono.just(new OwnedItemImage("old-key")));
        when(storage.upload(itemId, bytes, "image/png"))
                .thenReturn(Mono.just("new-key"));
        when(storage.previewUrl("new-key")).thenReturn(Mono.just("https://example.com/image"));
        when(clock.now()).thenReturn(now);
    }

    @Test
    void missingOwnedItemDoesNotReadOrUploadImage() {
        when(reader.findOwnedItem(itemId, 7L)).thenReturn(Mono.empty());
        var read = new AtomicBoolean();
        var command =
                new UploadImageCommand(
                        itemId,
                        Mono.fromSupplier(
                                () -> {
                                    read.set(true);
                                    return new ValidatedImage(bytes, "image/png");
                                }));
        var result = service.upload(command).block(Duration.ofSeconds(5));
        assertEquals("Item.NotFound", result.match(value -> "success", code -> code));
        assertTrue(!read.get());
        verifyNoInteractions(storage, writer);
    }

    @Test
    void successfulReplacementCleansOldImage() {
        prepareUpload();
        when(writer.replace(itemId, 7L, "old-key", "new-key", now)).thenReturn(Mono.just(true));
        when(storage.delete("old-key")).thenReturn(Mono.empty());
        var result = service.upload(command()).block(Duration.ofSeconds(5));
        assertTrue(result.isSuccess());
        verify(storage).delete("old-key");
        verify(storage, never()).delete("new-key");
    }

    @Test
    void conflictCleansNewImageAndPreservesOldImage() {
        prepareUpload();
        when(writer.replace(itemId, 7L, "old-key", "new-key", now)).thenReturn(Mono.just(false));
        when(storage.delete("new-key")).thenReturn(Mono.empty());
        var result = service.upload(command()).block(Duration.ofSeconds(5));
        assertEquals("Item.ImageUpdateConflict", result.match(value -> "success", code -> code));
        verify(storage).delete("new-key");
        verify(storage, never()).delete("old-key");
    }

    @Test
    void unknownDatabaseOutcomeDoesNotDeleteEitherImage() {
        prepareUpload();
        when(writer.replace(itemId, 7L, "old-key", "new-key", now))
                .thenReturn(Mono.error(new IllegalStateException("database unavailable")));
        assertThrows(
                IllegalStateException.class,
                () -> service.upload(command()).block(Duration.ofSeconds(5)));
        verify(storage, never()).delete(any());
    }
}
