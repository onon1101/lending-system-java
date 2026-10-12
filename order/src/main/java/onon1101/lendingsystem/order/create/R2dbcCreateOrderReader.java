package onon1101.lendingsystem.order.create;

import org.springframework.stereotype.Repository;
import org.springframework.r2dbc.core.DatabaseClient;

import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@Repository
public class R2dbcCreateOrderReader implements CreateOrderReader {

    private final DatabaseClient databaseClient;

    public R2dbcCreateOrderReader(
            DatabaseClient databaseClient
    ) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<OrderItem> findItemForUpdate(UUID itemId) {
        return databaseClient
                .sql("""
                        SELECT id,
                            owner_id,
                            name,
                            status
                        FROM items
                        WHERE public_id = :itemId
                        FOR UPDATE;
                        """)
                .bind("itemId", itemId)
                .map((row, metadata) ->
                    new OrderItem(
                            Objects.requireNonNull(row.get("id", Long.class)),
                            Objects.requireNonNull(row.get("owner_id", Long.class)),
                            Objects.requireNonNull(row.get("name", String.class)),
                            "available".equals(row.get("status", String.class))
                    )
                )
                .one();
    }
}
