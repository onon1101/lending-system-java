package onon1101.lendingsystem.order.returnOrder;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcReturnOrderReader implements ReturnOrderReader {

    private final DatabaseClient databaseClient;

    public R2dbcReturnOrderReader(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<OrderItem> lockItemForOrder(UUID orderId, long lenderId) {
        return databaseClient
                .sql(
                        """
                        SELECT i.id, i.owner_id, i.status
                        FROM items i
                        JOIN loan_records lr ON lr.item_id = i.id
                        WHERE lr.public_id = :orderId
                          AND lr.lender_id = :lenderId
                        FOR UPDATE OF i
                        """)
                .bind("orderId", orderId)
                .bind("lenderId", lenderId)
                .map(
                        (row, metadata) ->
                                new OrderItem(
                                        Objects.requireNonNull(row.get("id", Long.class)),
                                        Objects.requireNonNull(row.get("owner_id", Long.class)),
                                        Objects.requireNonNull(row.get("status", String.class))))
                .one();
    }

    @Override
    public Mono<LockedOrder> findOrderForUpdate(UUID orderId, long lenderId, long itemId) {
        return databaseClient
                .sql(
                        """
                        SELECT id, status, requested_at, updated_at, due_at
                        FROM loan_records
                        WHERE public_id = :orderId
                          AND lender_id = :lenderId
                          AND item_id = :itemId
                        FOR UPDATE
                        """)
                .bind("orderId", orderId)
                .bind("lenderId", lenderId)
                .bind("itemId", itemId)
                .map(
                        (row, metadata) ->
                                new LockedOrder(
                                        Objects.requireNonNull(row.get("id", Long.class)),
                                        Objects.requireNonNull(row.get("status", String.class)),
                                        Objects.requireNonNull(
                                                        row.get(
                                                                "requested_at",
                                                                OffsetDateTime.class))
                                                .toInstant(),
                                        Objects.requireNonNull(
                                                        row.get("updated_at", OffsetDateTime.class))
                                                .toInstant(),
                                        row.get("due_at", OffsetDateTime.class) == null
                                                ? null
                                                : row.get("due_at", OffsetDateTime.class)
                                                        .toInstant()))
                .one();
    }
}
