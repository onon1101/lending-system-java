package onon1101.lendingsystem.order.approve;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Objects;

@Repository
public class R2dbcApproveOrderWriter implements ApproveOrderWriter{

    private final DatabaseClient databaseClient;

    public R2dbcApproveOrderWriter(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Boolean> hasActiveOrder(long itemId) {

        return databaseClient
                .sql("""
                        SELECT EXISTS (
                            SELECT 1
                            FROM loan_records
                            WHERE item_id = :itemId
                                AND status IN ('approved', 'borrowed')
                        ) AS active_order_exists
                        """)
                .bind("itemId", itemId)
                .map((row, metadata) -> Objects.requireNonNull(
                        row.get("active_order_exists", Boolean.class)
                ))
                .one();
    }

    @Override
    public Mono<Boolean> approve(
            long orderId,
            long lenderId,
            long itemId,
            Instant approvedAt,
            Instant dueAt
    ) {


        return databaseClient
                .sql("""
                        UPDATE loan_records
                        SET status = 'approved',
                            approved_at = :approvedAt,
                            due_at = :dueAt,
                            updated_at = :approvedAt,
                            version = version + 1
                        WHERE id = :orderId
                            AND lender_id = :lenderId
                            AND item_id = :itemId
                            AND status = 'pending'
                        """)
                .bind("orderId", orderId)
                .bind("lenderId", lenderId)
                .bind("itemId", itemId)
                .bind("approvedAt", approvedAt.atOffset(ZoneOffset.UTC))
                .bind("dueAt", dueAt.atOffset(ZoneOffset.UTC))
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
                            "Expected to approve at most one order, affected" + rows + " rows."
                    );
                });
    }
}
