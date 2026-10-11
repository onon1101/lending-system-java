package onon1101.lendingsystem.item.publicList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import onon1101.lendingsystem.infrastructure.image.ImageStorage;
import onon1101.lendingsystem.item.domain.ItemAvailability;
import onon1101.lendingsystem.web.pagination.PaginationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class PublicListItemServiceTests {
    private final PublicListItemReader reader = mock(PublicListItemReader.class);
    @SuppressWarnings("unchecked")
    private final ObjectProvider<ImageStorage> storageProvider = mock(ObjectProvider.class);
    private final PublicListItemService service = new PublicListItemService(reader, storageProvider);

    @Test
    void includesEveryStatusAndDisablesGuestActionsWithoutStorage() {
        var pagination = new PaginationRequest(0, 20);
        when(reader.findAllPublicItems(pagination)).thenReturn(Flux.just(
                item(ItemAvailability.AVAILABLE, "cover"),
                item(ItemAvailability.UNAVAILABLE, null),
                item(ItemAvailability.ARCHIVED, null)));

        var response = service.listPublic(pagination).block(Duration.ofSeconds(5));
        assertNotNull(response);
        assertEquals(3, response.items().size());
        assertEquals(ItemAvailability.ARCHIVED, response.items().get(2).availability());
        for (var item : response.items()) {
            assertFalse(item.ownedByCurrentUser());
            assertFalse(item.canRequestBorrow());
            assertNull(item.imageUrl());
        }
    }

    @Test
    void trimsLookaheadAndSignsOnlyReturnedImages() {
        var pagination = new PaginationRequest(0, 1);
        var storage = mock(ImageStorage.class);
        when(storageProvider.getIfAvailable()).thenReturn(storage);
        when(storage.previewUrl("cover")).thenReturn(Mono.just("https://example.test/cover"));
        when(reader.findAllPublicItems(pagination)).thenReturn(Flux.just(
                item(ItemAvailability.AVAILABLE, "cover"),
                item(ItemAvailability.UNAVAILABLE, "lookahead")));

        var response = service.listPublic(pagination).block(Duration.ofSeconds(5));
        assertNotNull(response);
        assertEquals(1, response.items().size());
        assertEquals("https://example.test/cover", response.items().getFirst().imageUrl());
        assertTrue(response.response().hasNext());
        verify(storage, never()).previewUrl("lookahead");
    }

    private static PublicListedItem item(ItemAvailability availability, String key) {
        return new PublicListedItem(UUID.randomUUID(), UUID.randomUUID(), "owner",
                "item", "description", availability, Instant.EPOCH, Instant.EPOCH, key);
    }
}
