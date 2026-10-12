package onon1101.lendingsystem.create;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Repository
public class R2dbcCreateOrderWriter implements CreateOrderWriter {

    private final DatabaseClient databaseClient;

    public R2dbcCreateOrderWriter(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Boolean> create(NewOrder order) {
        OffsetDateTime requestedAt = order
                .requestedAt()
                .atOffset(ZoneOffset.UTC);

        return databaseClient
                .sql("""
                        INSERT INTO loan_records (
                            public_id,
                            item_id,
                            borrower_id,
                            lender_id,
                            item_name_snapshot,
                            status,
                            planned_borrow_date,
                            planned_return_date,
                            requested_at,
                            created_at,
                            updated_at
                        )
                        VALUES (
                            :orderId,
                            :itemId,
                            :borrowerId,
                            :lenderId,
                            :itemNameSnapshot,
                            :status,
                            :plannedBorrowDate,
                            :plannedReturnDate,
                            :requestedAt,
                            :createdAt,
                            :updatedAt
                        )
                        ON CONFLICT (item_id, borrower_id)
                            WHERE status IN ('pending', 'approved', 'borrowed')
                        DO NOTHING;
                        """)
                .bind("orderId", order.orderId())
                .bind("itemId", order.itemId())
                .bind("borrowerId", order.borrowerId())
                .bind("lenderId", order.lenderId())
                .bind("itemNameSnapshot", order.itemNameSnapshot())
                .bind("status", CreateOrderResult.Status.PENDING.value())
                .bind("plannedBorrowDate", order.plannedBorrowDate())
                .bind("plannedReturnDate", order.plannedReturnDate())
                .bind("requestedAt", requestedAt)
                .bind("createdAt", requestedAt)
                .bind("updatedAt", requestedAt)
                .fetch()
                .rowsUpdated()
                .map(rows -> {
                    if (rows == 1L) {
                        return true;
                    }

                    if (rows == 0L) {
                        return false;
                    }

                    throw new IllegalStateException(
                            "Expected to create at most one order, but " +
                                    "affected" + rows + " rows.");
                });
    }
}
