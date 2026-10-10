package onon1101.lendingsystem.infrastructure.storage.rustfs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.health.contributor.Status;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

class RustfsConfigurationTests {
    private final RustfsProperties properties =
            new RustfsProperties(
                    true,
                    "http://localhost:9000",
                    "test-access",
                    "test-secret",
                    "lending-test",
                    "us-east-1");

    @Test
    void signsBrowserUrlsUsingRustfsEndpointAndPathStyle() {
        try (var presigner = new RustfsConfiguration().rustfsPresigner(properties)) {
            URI url =
                    presigner
                            .presignGetObject(
                                    GetObjectPresignRequest.builder()
                                            .signatureDuration(Duration.ofMinutes(15))
                                            .getObjectRequest(
                                                    GetObjectRequest.builder()
                                                            .bucket(properties.bucket())
                                                            .key("items/test/cover.jpg")
                                                            .build())
                                            .build())
                            .url()
                            .toURI();
            assertEquals("localhost", url.getHost());
            assertEquals(9000, url.getPort());
            assertEquals("/lending-test/items/test/cover.jpg", url.getPath());
            assertTrue(url.getQuery().contains("X-Amz-Expires=900"));
            assertTrue(url.getQuery().contains("us-east-1"));
        } catch (java.net.URISyntaxException exception) {
            throw new AssertionError(exception);
        }
    }

    @Test
    void reportsAccessibleBucketAsHealthy() {
        S3Client client = mock(S3Client.class);
        when(client.headBucket(any(HeadBucketRequest.class)))
                .thenReturn(HeadBucketResponse.builder().build());
        assertEquals(Status.UP, new RustfsHealthIndicator(client, properties).health().getStatus());
    }

    @Test
    void reportsMissingBucketAndDeniedAccessAsUnhealthy() {
        S3Client client = mock(S3Client.class);
        when(client.headBucket(any(HeadBucketRequest.class)))
                .thenThrow(S3Exception.builder().statusCode(404).build());
        var missing = new RustfsHealthIndicator(client, properties).health();
        assertEquals(Status.DOWN, missing.getStatus());
        assertEquals("Required bucket does not exist", missing.getDetails().get("reason"));

        doThrow(S3Exception.builder().statusCode(403).build())
                .when(client)
                .headBucket(any(HeadBucketRequest.class));
        var denied = new RustfsHealthIndicator(client, properties).health();
        assertEquals(Status.DOWN, denied.getStatus());
        assertEquals("Bucket access failed", denied.getDetails().get("reason"));
    }
}
