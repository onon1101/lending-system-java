package onon1101.lendingsystem.infrastructure.image;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.Duration;
import java.util.UUID;
import onon1101.lendingsystem.infrastructure.storage.rustfs.RustfsProperties;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

class ImageStorageTests {
    private final S3Client client = mock(S3Client.class);
    private final ImageStorage storage =
            new ImageStorage(
                    client,
                    mock(S3Presigner.class),
                    new RustfsProperties(
                            true,
                            "http://localhost:9000",
                            "test-access",
                            "test-secret",
                            "lending-test",
                            "us-east-1"));

    @Test
    void uploadsBytesToConfiguredBucketWithGeneratedImageKey() throws Exception {
        UUID itemId = UUID.randomUUID();
        byte[] bytes = {1, 2, 3};
        String key = storage.upload(itemId, bytes, "image/jpeg").block(Duration.ofSeconds(5));
        var request = ArgumentCaptor.forClass(PutObjectRequest.class);
        var body = ArgumentCaptor.forClass(RequestBody.class);
        verify(client).putObject(request.capture(), body.capture());
        assertEquals("lending-test", request.getValue().bucket());
        assertEquals("image/jpeg", request.getValue().contentType());
        assertEquals(key, request.getValue().key());
        assertTrue(key.startsWith("items/" + itemId + "/"));
        assertTrue(key.endsWith(".jpg"));
        try (var input = body.getValue().contentStreamProvider().newStream()) {
            assertArrayEquals(bytes, input.readAllBytes());
        }
    }

    @Test
    void rejectsUnsupportedTypeBeforeCallingStorage() {
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        storage.upload(UUID.randomUUID(), new byte[] {1}, "image/svg+xml")
                                .block(Duration.ofSeconds(5)));
        verifyNoInteractions(client);
    }

    @Test
    void deletesObjectFromConfiguredBucket() {
        storage.delete("items/test/cover.jpg").block(Duration.ofSeconds(5));
        verify(client)
                .deleteObject(
                        DeleteObjectRequest.builder()
                                .bucket("lending-test")
                                .key("items/test/cover.jpg")
                                .build());
    }
}
