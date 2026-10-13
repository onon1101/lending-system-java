package onon1101.lendingsystem.order.returnOrder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;
import onon1101.lendingsystem.order.returnOrder.error.ReturnOrderDomainError;
import onon1101.lendingsystem.shared.domain.Result;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

class ReturnOrderControllerTests {
    private final ReturnOrderService service = mock(ReturnOrderService.class);
    private final WebTestClient client =
            WebTestClient.bindToController(new ReturnOrderController(service)).build();
    private final UUID orderId = UUID.randomUUID();
    private final String uri = "/api/v1/orders/" + orderId + "/return";

    @Test
    void postWithoutBodySucceeds() {
        when(service.returnOrder(new ReturnOrderCommand(orderId)))
                .thenReturn(
                        Mono.just(
                                Result.success(
                                        new ReturnOrderResult(
                                                orderId,
                                                "returned",
                                                Instant.parse("2026-10-13T04:00:00Z")))));
        client.post().uri(uri).exchange().expectStatus().isOk();
        verify(service).returnOrder(new ReturnOrderCommand(orderId));
    }

    @Test
    void inaccessibleOrderReturnsNotFound() {
        when(service.returnOrder(new ReturnOrderCommand(orderId)))
                .thenReturn(Mono.just(Result.failure(ReturnOrderDomainError.notFound())));
        client.post().uri(uri).exchange().expectStatus().isNotFound();
    }

    @Test
    void invalidStatusReturnsConflict() {
        when(service.returnOrder(new ReturnOrderCommand(orderId)))
                .thenReturn(Mono.just(Result.failure(ReturnOrderDomainError.invalidStatus())));
        client.post().uri(uri).exchange().expectStatus().isEqualTo(409);
    }

    @Test
    void malformedOrderIdReturnsBadRequest() {
        client.post()
                .uri("/api/v1/orders/not-a-uuid/return")
                .exchange()
                .expectStatus()
                .isBadRequest();
        verifyNoInteractions(service);
    }
}
