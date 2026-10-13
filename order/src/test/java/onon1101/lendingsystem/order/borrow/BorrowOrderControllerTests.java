package onon1101.lendingsystem.order.borrow;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;
import onon1101.lendingsystem.order.borrow.error.BorrowOrderDomainError;
import onon1101.lendingsystem.shared.domain.Result;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

class BorrowOrderControllerTests {
    private final BorrowOrderService service = mock(BorrowOrderService.class);
    private final WebTestClient client =
            WebTestClient.bindToController(new BorrowOrderController(service)).build();
    private final UUID orderId = UUID.randomUUID();
    private final String uri = "/api/v1/orders/" + orderId + "/borrow";

    @Test
    void postWithoutBodySucceeds() {
        when(service.borrow(new BorrowOrderCommand(orderId)))
                .thenReturn(
                        Mono.just(
                                Result.success(
                                        new BorrowOrderResult(
                                                orderId,
                                                "borrowed",
                                                Instant.parse("2026-10-13T04:00:00Z")))));
        client.post().uri(uri).exchange().expectStatus().isOk();
        verify(service).borrow(new BorrowOrderCommand(orderId));
    }

    @Test
    void inaccessibleOrderReturnsNotFound() {
        when(service.borrow(new BorrowOrderCommand(orderId)))
                .thenReturn(Mono.just(Result.failure(BorrowOrderDomainError.notFound())));
        client.post().uri(uri).exchange().expectStatus().isNotFound();
    }

    @Test
    void invalidStatusReturnsConflict() {
        when(service.borrow(new BorrowOrderCommand(orderId)))
                .thenReturn(Mono.just(Result.failure(BorrowOrderDomainError.invalidStatus())));
        client.post().uri(uri).exchange().expectStatus().isEqualTo(409);
    }

    @Test
    void malformedOrderIdReturnsBadRequest() {
        client.post()
                .uri("/api/v1/orders/not-a-uuid/borrow")
                .exchange()
                .expectStatus()
                .isBadRequest();
        verifyNoInteractions(service);
    }
}
