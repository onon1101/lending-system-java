package onon1101.lendingsystem.item.update;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.UUID;
import onon1101.lendingsystem.item.domain.Item;
import onon1101.lendingsystem.item.domain.ItemAvailability;
import onon1101.lendingsystem.item.domain.ItemDescription;
import onon1101.lendingsystem.item.domain.ItemId;
import onon1101.lendingsystem.item.domain.ItemName;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcUpdateItemRepository implements UpdateItemReader, UpdateItemWriter {

    private final DatabaseClient databaseClient;

    public R2dbcUpdateItemRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Item> finOwnedItem(UUID itemId, long ownerId) {
        return databaseClient
                .sql(
                        """
                        SELECT
                        	public_id,
                        	owner_id,
                        	name,
                        	description,
                        	status,
                        	created_at,
                        	updated_at
                        FROM items
                        WHERE public_id = :publicId
                        	AND owner_id = :ownerId
                        	AND status <> 'archived';
                        """)
                .bind("publicId", itemId)
                .bind("ownerId", ownerId)
                .map(
                        (row, metadata) ->
                                Item.reconstitute(
                                        ItemId.of(require(row.get("public_id", UUID.class))),
                                        require(row.get("owner_id", Long.class)),
                                        ItemName.of(require(row.get("name", String.class))),
                                        ItemDescription.of(row.get("description", String.class)),
                                        ItemAvailability.valueOf(
                                                require(row.get("status", String.class))
                                                        .toUpperCase(Locale.ROOT)),
                                        require(row.get("created_at", OffsetDateTime.class))
                                                .toInstant(),
                                        require(row.get("updated_at", OffsetDateTime.class))
                                                .toInstant()))
                .one();
    }

    @Override
    public Mono<Boolean> update(Item item) {
        return databaseClient
                .sql(
                        """
                                UPDATE items
                                SET name = :name,
                                    description = :description,
                                    updated_at = :updatedAt
                                WHERE public_id = :itemId
                                AND owner_id = :ownerId
                                AND status <> 'archived'
                                """)
                .bind("name", item.name().value())
                .bind("description", item.description().value())
                .bind("updatedAt", item.updatedAt().atOffset(ZoneOffset.UTC))
                .bind("itemId", item.id().value())
                .bind("ownerId", item.ownerId())
                .fetch()
                .rowsUpdated()
                .map(rows -> rows == 1);
    }

    private static <T> T require(T value) {
        if (value == null) throw new IllegalStateException("Required item column was null");
        return value;
    }
}
