package onon1101.lendingsystem.order.borrow;

import java.time.Instant;
import java.time.ZoneOffset;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcBorrowOrderWriter implements BorrowOrderWriter {

    private final DatabaseClient databaseClient;

    public R2dbcBorrowOrderWriter(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Boolean> borrow(long orderId, long lenderId, long itemId, Instant borrowedAt) {
        return databaseClient
                .sql(
                        """
                        UPDATE loan_records
                        SET status = 'borrowed',
                            borrowed_at = :borrowedAt,
                            updated_at = :borrowedAt,
                            version = version + 1
                        WHERE id = :orderId
                          AND lender_id = :lenderId
                          AND item_id = :itemId
                          AND status IN ('approved')
                        """)
                .bind("orderId", orderId)
                .bind("lenderId", lenderId)
                .bind("itemId", itemId)
                .bind("borrowedAt", borrowedAt.atOffset(ZoneOffset.UTC))
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
                                    "Expected to borrow at most one order, affected "
                                            + rows
                                            + " rows.");
                        });
    }
}
