package onon1101.lendingsystem.item.list;
import onon1101.lendingsystem.shared.pagination.PageSpec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import onon1101.lendingsystem.application.currentuser.CurrentUserContext;
import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;
import onon1101.lendingsystem.item.domain.ItemAvailability;
import onon1101.lendingsystem.web.pagination.PaginationRequest;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class ListItemsServiceTests {
    private final ListItemsReader reader = mock(ListItemsReader.class);
    private final ItemPreviewUrlReader previews = mock(ItemPreviewUrlReader.class);
    private final CurrentUserProvider currentUser = () -> Mono.just(
            new CurrentUserContext(8, UUID.randomUUID(), "viewer", "viewer@test", "active"));
    private final ListItemsService service = new ListItemsService(reader, currentUser, previews);

    @Test
    void trimsLookaheadBeforeResolvingImagesAndPreservesHttpContract() {
        var query = new ListItemsQuery(new PageSpec(2, 1));
        var first = item("cover");
        when(reader.findVisibleItems(8, query)).thenReturn(Flux.just(first, item("lookahead")));
        when(previews.resolve("cover")).thenReturn(Mono.just("https://example.test/cover"));

        var response = new ListItemsController(service)
                .list(new PaginationRequest(2, 1)).block(Duration.ofSeconds(5));
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        var body = response.getBody().data();
        assertNotNull(body);
        assertEquals(1, body.items().size());
        assertEquals(first.itemId(), body.items().getFirst().itemId());
        assertEquals("https://example.test/cover", body.items().getFirst().imageUrl());
        assertTrue(body.items().getFirst().canRequestBorrow());
        assertEquals(2, body.response().page());
        assertEquals(1, body.response().size());
        assertTrue(body.response().hasNext());
        verify(previews, never()).resolve("lookahead");
    }

    @Test
    void keepsItemsWithoutPreviewAndTheirOrderOnAFullLastPage() {
        var query = new ListItemsQuery(new PageSpec(0, 2));
        var first = item(null);
        var second = item("missing");
        when(reader.findVisibleItems(8, query)).thenReturn(Flux.just(first, second));
        when(previews.resolve(null)).thenReturn(Mono.empty());
        when(previews.resolve("missing")).thenReturn(Mono.empty());
        var result = service.list(query).block(Duration.ofSeconds(5));
        assertNotNull(result);
        assertFalse(result.hasNext());
        assertEquals(first, result.items().get(0).item());
        assertEquals(second, result.items().get(1).item());
        assertTrue(result.items().stream().allMatch(item -> item.imageUrl() == null));
    }

    @Test
    void returnsAnEmptyPageWithoutResolvingImages() {
        var query = new ListItemsQuery(new PageSpec(0, 20));
        when(reader.findVisibleItems(8, query)).thenReturn(Flux.empty());
        var result = service.list(query).block(Duration.ofSeconds(5));
        assertNotNull(result);
        assertTrue(result.items().isEmpty());
        assertFalse(result.hasNext());
        verifyNoInteractions(previews);
    }

    @Test
    void propagatesPreviewFailures() {
        var query = new ListItemsQuery(new PageSpec(0, 1));
        var failure = new IllegalStateException("Storage unavailable");
        when(reader.findVisibleItems(8, query)).thenReturn(Flux.just(item("cover")));
        when(previews.resolve("cover")).thenReturn(Mono.error(failure));
        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> service.list(query).block(Duration.ofSeconds(5))));
    }

    private static ListedItem item(String key) {
        return new ListedItem(UUID.randomUUID(), UUID.randomUUID(), "owner", "item",
                "description", ItemAvailability.AVAILABLE, false, Instant.EPOCH, Instant.EPOCH, key);
    }
}
