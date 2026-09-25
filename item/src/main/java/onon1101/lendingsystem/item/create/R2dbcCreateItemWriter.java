package onon1101.lendingsystem.item.create;

import java.time.ZoneOffset;
import java.util.Locale;
import onon1101.lendingsystem.item.domain.Item;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcCreateItemWriter implements CreateItemWriter {

    private final DatabaseClient databaseClient;

    public R2dbcCreateItemWriter(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Void> create(Item item) {
        String sql =
                """
                INSERT INTO items (
                    public_id,
                    owner_id,
                    name,
                    description,
                    status,
                    created_at,
                    updated_at
                )
                VALUES (
                    :publicId,
                    :ownerId,
                    :name,
                    :description,
                    :status,
                    :createdAt,
                    :updatedAt
                )
                """;

        return databaseClient
                .sql(sql)
                .bind("publicId", item.id().value())
                .bind("ownerId", item.ownerId())
                .bind("name", item.name().value())
                .bind("description", item.description().value())
                .bind("status", item.availability().name().toLowerCase(Locale.ROOT))
                .bind("createdAt", item.createdAt().atOffset(ZoneOffset.UTC))
                .bind("updatedAt", item.updatedAt().atOffset(ZoneOffset.UTC))
                .fetch()
                .rowsUpdated()
                .flatMap(
                        rows ->
                                rows == 1
                                        ? Mono.<Void>empty()
                                        : Mono.error(
                                                new IllegalStateException(
                                                        "Expected to create one item, but affected "
                                                                + rows
                                                                + " rows.")));
    }
}
