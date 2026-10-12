package onon1101.lendingsystem.item.publicList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import onon1101.lendingsystem.item.domain.ItemAvailability;
import onon1101.lendingsystem.web.pagination.PaginationRequest;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class PublicListItemServiceTests {
    private final PublicListItemReader reader = mock(PublicListItemReader.class);
    private final PublicItemPreviewUrlReader previews = mock(PublicItemPreviewUrlReader.class);
    private final PublicListItemService service = new PublicListItemService(reader, previews);

    @Test
    void preservesPublicHttpContractAndIncludesEveryStatus() {
        var query = new PublicListItemsQuery(0, 20);
        when(reader.findAllPublicItems(query))
                .thenReturn(
                        Flux.just(
                                item(ItemAvailability.AVAILABLE, "available"),
                                item(ItemAvailability.UNAVAILABLE, "unavailable"),
                                item(ItemAvailability.ARCHIVED, "archived")));
        when(previews.resolve(anyString())).thenReturn(Mono.empty());
        var response =
                new PublicListItemController(service)
                        .listPublic(new PaginationRequest(0, 20))
                        .block(Duration.ofSeconds(5));
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        var envelope = response.getBody();
        assertNotNull(envelope);
        assertTrue(envelope.isSuccess());
        var body = envelope.data();
        assertNotNull(body);
        assertEquals(3, body.items().size());
        assertEquals(ItemAvailability.ARCHIVED, body.items().get(2).availability());
        assertEquals(0, body.response().page());
        assertEquals(20, body.response().size());
        assertFalse(body.response().hasNext());
        for (var item : body.items()) {
            assertFalse(item.ownedByCurrentUser());
            assertFalse(item.canRequestBorrow());
            assertNull(item.imageUrl());
        }
    }

    @Test
    void trimsLookaheadBeforeResolvingImages() {
        var query = new PublicListItemsQuery(2, 1);
        var first = item(ItemAvailability.AVAILABLE, "cover");
        when(reader.findAllPublicItems(query))
                .thenReturn(Flux.just(first, item(ItemAvailability.UNAVAILABLE, "lookahead")));
        when(previews.resolve("cover")).thenReturn(Mono.just("https://example.test/cover"));
        var result = service.listPublic(query).block(Duration.ofSeconds(5));
        assertNotNull(result);
        assertEquals(1, result.items().size());
        assertEquals(first, result.items().getFirst().item());
        assertEquals("https://example.test/cover", result.items().getFirst().imageUrl());
        assertEquals(2, result.page());
        assertEquals(1, result.size());
        assertTrue(result.hasNext());
        verify(previews, never()).resolve("lookahead");
    }

    @Test
    void keepsItemsWithoutPreviewAndPreservesOrder() {
        var query = new PublicListItemsQuery(0, 2);
        var first = item(ItemAvailability.AVAILABLE, null);
        var second = item(ItemAvailability.UNAVAILABLE, "missing");
        when(reader.findAllPublicItems(query)).thenReturn(Flux.just(first, second));
        when(previews.resolve(null)).thenReturn(Mono.empty());
        when(previews.resolve("missing")).thenReturn(Mono.empty());
        var result = service.listPublic(query).block(Duration.ofSeconds(5));
        assertNotNull(result);
        assertEquals(2, result.items().size());
        assertEquals(first, result.items().get(0).item());
        assertEquals(second, result.items().get(1).item());
        assertNull(result.items().get(0).imageUrl());
        assertNull(result.items().get(1).imageUrl());
        assertFalse(result.hasNext());
    }

    @Test
    void returnsEmptyPageWithoutResolvingImages() {
        var query = new PublicListItemsQuery(0, 20);
        when(reader.findAllPublicItems(query)).thenReturn(Flux.empty());
        var result = service.listPublic(query).block(Duration.ofSeconds(5));
        assertNotNull(result);
        assertTrue(result.items().isEmpty());
        assertFalse(result.hasNext());
        verifyNoInteractions(previews);
    }

    @Test
    void propagatesPreviewFailures() {
        var query = new PublicListItemsQuery(0, 1);
        var failure = new IllegalStateException("Storage unavailable");
        when(reader.findAllPublicItems(query))
                .thenReturn(Flux.just(item(ItemAvailability.AVAILABLE, "cover")));
        when(previews.resolve("cover")).thenReturn(Mono.error(failure));
        var actual =
                assertThrows(
                        IllegalStateException.class,
                        () -> service.listPublic(query).block(Duration.ofSeconds(5)));
        assertSame(failure, actual);
    }

    private static PublicListedItem item(ItemAvailability availability, String key) {
        return new PublicListedItem(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "owner",
                "item",
                "description",
                availability,
                Instant.EPOCH,
                Instant.EPOCH,
                key);
    }
}
