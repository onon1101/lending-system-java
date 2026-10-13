package onon1101.lendingsystem.order.cancel;

import java.time.Instant;
import java.time.ZoneOffset;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcCancelOrderWriter implements CancelOrderWriter {

    private final DatabaseClient databaseClient;

    public R2dbcCancelOrderWriter(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Boolean> cancel(long orderId, long borrowerId, long itemId, Instant closedAt) {
        return databaseClient
                .sql(
                        """
                        UPDATE loan_records
                        SET status = 'cancelled',
                            closed_at = :closedAt,
                            updated_at = :closedAt,
                            version = version + 1
                        WHERE id = :orderId
                          AND borrower_id = :borrowerId
                          AND item_id = :itemId
                          AND status IN ('pending', 'approved')
                        """)
                .bind("orderId", orderId)
                .bind("borrowerId", borrowerId)
                .bind("itemId", itemId)
                .bind("closedAt", closedAt.atOffset(ZoneOffset.UTC))
                .fetch()
                .rowsUpdated()
                .map(
                        rows -> {
                            if (rows == 1L) {
                                return true;
                            }

                            if (rows == 0L) {
                                return false;
                            }

                            throw new IllegalStateException(
                                    "Expected to cancel at most one order, affected "
                                            + rows
                                            + " rows.");
                        });
    }
}
