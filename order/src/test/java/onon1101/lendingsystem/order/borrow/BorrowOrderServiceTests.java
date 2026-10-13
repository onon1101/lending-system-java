package onon1101.lendingsystem.order.borrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;
import onon1101.lendingsystem.application.currentuser.CurrentUserContext;
import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;
import onon1101.lendingsystem.shared.domain.Result;
import onon1101.lendingsystem.shared.time.IClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

class BorrowOrderServiceTests {
    private final BorrowOrderReader reader = mock(BorrowOrderReader.class);
    private final BorrowOrderWriter writer = mock(BorrowOrderWriter.class);
    private final CurrentUserProvider users = mock(CurrentUserProvider.class);
    private final IClock clock = mock(IClock.class);
    private final UUID orderId = UUID.randomUUID();
    private final Instant now = Instant.parse("2026-10-13T04:00:00Z");
    private final BorrowOrderService service = new BorrowOrderService(reader, writer, users, clock);

    @BeforeEach
    void setUp() {
        when(users.getCurrentUser())
                .thenReturn(
                        Mono.just(
                                new CurrentUserContext(
                                        10L,
                                        UUID.randomUUID(),
                                        "user",
                                        "user@example.com",
                                        "active")));
        when(clock.now()).thenReturn(now);
        when(reader.lockItemForOrder(orderId, 10L))
                .thenReturn(Mono.just(new BorrowOrderReader.OrderItem(20L, 10L, "available")));
        order("approved", now.plusSeconds(3600));
        when(writer.borrow(30L, 10L, 20L, now)).thenReturn(Mono.just(true));
    }

    private void order(String status, Instant dueAt) {
        when(reader.findOrderForUpdate(orderId, 10L, 20L))
                .thenReturn(
                        Mono.just(
                                new BorrowOrderReader.LockedOrder(
                                        30L,
                                        status,
                                        now.minusSeconds(100),
                                        now.minusSeconds(10),
                                        dueAt)));
    }

    private Result<BorrowOrderResult> execute() {
        return service.borrow(new BorrowOrderCommand(orderId)).block();
    }

    @Test
    void transitionsAllowedOrder() {
        Result<BorrowOrderResult> result = execute();
        assertTrue(result.isSuccess());
        assertEquals("borrowed", result.match(BorrowOrderResult::status, code -> code));
        verify(writer).borrow(30L, 10L, 20L, now);
    }

    @Test
    void inaccessibleOrderIsNotFound() {
        when(reader.lockItemForOrder(orderId, 10L)).thenReturn(Mono.empty());
        assertEquals("Order.NotFound", execute().match(value -> "success", code -> code));
        verifyNoInteractions(writer);
    }

    @Test
    void missingLockedOrderIsNotFound() {
        when(reader.findOrderForUpdate(orderId, 10L, 20L)).thenReturn(Mono.empty());
        assertEquals("Order.NotFound", execute().match(value -> "success", code -> code));
        verifyNoInteractions(writer);
    }

    @Test
    void invalidStatusDoesNotWrite() {
        order("returned", now.plusSeconds(3600));
        assertEquals("Order.InvalidStatus", execute().match(value -> "success", code -> code));
        verifyNoInteractions(writer);
    }

    @Test
    void failedConditionalUpdateIsConflict() {
        when(writer.borrow(30L, 10L, 20L, now)).thenReturn(Mono.just(false));
        assertEquals("Order.InvalidStatus", execute().match(value -> "success", code -> code));
    }

    @Test
    void databaseFailureRemainsAnError() {
        IllegalStateException failure = new IllegalStateException("database unavailable");
        when(writer.borrow(30L, 10L, 20L, now)).thenReturn(Mono.error(failure));
        assertSame(failure, assertThrows(IllegalStateException.class, this::execute));
    }

    @Test
    void nonOwnerCannotOperateOrder() {
        when(reader.lockItemForOrder(orderId, 10L))
                .thenReturn(Mono.just(new BorrowOrderReader.OrderItem(20L, 99L, "available")));
        assertEquals("Order.NotFound", execute().match(value -> "success", code -> code));
        verifyNoInteractions(writer);
    }

    @Test
    void dueTimeMustBeStrictlyAfterHandover() {
        order("approved", now);
        assertEquals("Order.DueAtExpired", execute().match(value -> "success", code -> code));
        verifyNoInteractions(writer);
    }

    @Test
    void unavailableItemCannotBeHandedOver() {
        when(reader.lockItemForOrder(orderId, 10L))
                .thenReturn(Mono.just(new BorrowOrderReader.OrderItem(20L, 10L, "archived")));
        assertEquals("Order.ItemUnavailable", execute().match(value -> "success", code -> code));
        verifyNoInteractions(writer);
    }
}
