package onon1101.lendingsystem.order.cancel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;
import onon1101.lendingsystem.order.cancel.error.CancelOrderDomainError;
import onon1101.lendingsystem.shared.domain.Result;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

class CancelOrderControllerTests {
    private final CancelOrderService service = mock(CancelOrderService.class);
    private final WebTestClient client =
            WebTestClient.bindToController(new CancelOrderController(service)).build();
    private final UUID orderId = UUID.randomUUID();
    private final String uri = "/api/v1/orders/" + orderId + "/cancel";

    @Test
    void postWithoutBodySucceeds() {
        when(service.cancel(new CancelOrderCommand(orderId)))
                .thenReturn(
                        Mono.just(
                                Result.success(
                                        new CancelOrderResult(
                                                orderId,
                                                "cancelled",
                                                Instant.parse("2026-10-13T04:00:00Z")))));
        client.post().uri(uri).exchange().expectStatus().isOk();
        verify(service).cancel(new CancelOrderCommand(orderId));
    }

    @Test
    void inaccessibleOrderReturnsNotFound() {
        when(service.cancel(new CancelOrderCommand(orderId)))
                .thenReturn(Mono.just(Result.failure(CancelOrderDomainError.notFound())));
        client.post().uri(uri).exchange().expectStatus().isNotFound();
    }

    @Test
    void invalidStatusReturnsConflict() {
        when(service.cancel(new CancelOrderCommand(orderId)))
                .thenReturn(Mono.just(Result.failure(CancelOrderDomainError.invalidStatus())));
        client.post().uri(uri).exchange().expectStatus().isEqualTo(409);
    }

    @Test
    void malformedOrderIdReturnsBadRequest() {
        client.post()
                .uri("/api/v1/orders/not-a-uuid/cancel")
                .exchange()
                .expectStatus()
                .isBadRequest();
        verifyNoInteractions(service);
    }
}
