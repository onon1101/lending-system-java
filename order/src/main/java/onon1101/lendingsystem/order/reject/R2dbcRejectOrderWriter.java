package onon1101.lendingsystem.order.reject;


import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.ZoneOffset;

@Repository
public class R2dbcRejectOrderWriter implements RejectOrderWriter {

    private final DatabaseClient databaseClient;

    public R2dbcRejectOrderWriter(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Boolean> reject(
            long orderId,
            long lenderId,
            long itemId,
            Instant closedAt
    ) {
        return databaseClient
                .sql("""
                        UPDATE loan_records
                        SET status = 'rejected',
                            closed_at = :closedAt,
                            updated_at = :closedAt,
                            version = version + 1
                        WHERE id = :orderId
                          AND lender_id = :lenderId
                          AND item_id = :itemId
                          AND status = 'pending'
                        """)
                .bind("orderId", orderId)
                .bind("lenderId", lenderId)
                .bind("itemId", itemId)
                .bind("closedAt", closedAt.atOffset(ZoneOffset.UTC))
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
                            "Expected to reject at most one order, affected "
                                    + rows + " rows."
                    );
                });
    }
}
