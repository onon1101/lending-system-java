package onon1101.lendingsystem.order.returnOrder;

import java.time.Instant;
import java.time.ZoneOffset;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcReturnOrderWriter implements ReturnOrderWriter {

    private final DatabaseClient databaseClient;

    public R2dbcReturnOrderWriter(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Boolean> returnOrder(long orderId, long lenderId, long itemId, Instant returnedAt) {
        return databaseClient
                .sql(
                        """
                        UPDATE loan_records
                        SET status = 'returned',
                            returned_at = :returnedAt,
                            closed_at = :returnedAt,
                            updated_at = :returnedAt,
                            version = version + 1
                        WHERE id = :orderId
                          AND lender_id = :lenderId
                          AND item_id = :itemId
                          AND status IN ('borrowed')
                        """)
                .bind("orderId", orderId)
                .bind("lenderId", lenderId)
                .bind("itemId", itemId)
                .bind("returnedAt", returnedAt.atOffset(ZoneOffset.UTC))
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
                                    "Expected to returnOrder at most one order, affected "
                                            + rows
                                            + " rows.");
                        });
    }
}
