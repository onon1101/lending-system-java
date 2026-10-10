package onon1101.lendingsystem.image.uploadimage;

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
import java.util.concurrent.atomic.AtomicBoolean;
import onon1101.lendingsystem.application.currentuser.CurrentUserContext;
import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;
import onon1101.lendingsystem.infrastructure.image.ImageStorage;
import onon1101.lendingsystem.shared.time.IClock;
import onon1101.lendingsystem.image.uploadimage.repository.UploadItemImageReader;
import onon1101.lendingsystem.image.uploadimage.repository.UploadItemImageWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

class UploadItemImageServiceTests {
    private final UUID itemId = UUID.randomUUID();
    private final Instant now = Instant.parse("2026-10-10T00:00:00Z");
    private final UploadItemImageReader reader = mock(UploadItemImageReader.class);
    private final UploadItemImageWriter writer = mock(UploadItemImageWriter.class);
    private final CurrentUserProvider users = mock(CurrentUserProvider.class);
    private final ImageStorage storage = mock(ImageStorage.class);
    private final IClock clock = mock(IClock.class);
    private final UploadItemImageService service =
            new UploadItemImageService(reader, writer, users, storage, clock);
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

    private UploadItemImageCommand command() {
        return new UploadItemImageCommand(
                itemId, Mono.just(new ValidatedItemImage(bytes, "image/png")));
    }

    private void prepareUpload() {
        when(reader.findOwnedItem(itemId, 7L)).thenReturn(Mono.just(new OwnedItemImage("old-key")));
        when(storage.uploadValidatedImage(itemId, bytes, "image/png"))
                .thenReturn(Mono.just("new-key"));
        when(storage.previewUrl("new-key")).thenReturn(Mono.just("https://example.com/image"));
        when(clock.now()).thenReturn(now);
    }

    @Test
    void missingOwnedItemDoesNotReadOrUploadImage() {
        when(reader.findOwnedItem(itemId, 7L)).thenReturn(Mono.empty());
        var read = new AtomicBoolean();
        var command =
                new UploadItemImageCommand(
                        itemId,
                        Mono.fromSupplier(
                                () -> {
                                    read.set(true);
                                    return new ValidatedItemImage(bytes, "image/png");
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
