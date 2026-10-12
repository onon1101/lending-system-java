package onon1101.lendingsystem.item.publicList;

import io.r2dbc.spi.Row;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;
import onon1101.lendingsystem.item.domain.ItemAvailability;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public class R2dbcPublicListItemReader implements PublicListItemReader {
    private static final String FIND_ALL_PUBLIC_ITEMS =
            """
            SELECT
                item.public_id AS item_public_id,
                owner.public_id AS owner_public_id,
                owner.username AS owner_username,
                item.name,
                item.description,
                item.status,
                item.created_at,
                item.updated_at,
                cover.object_key AS cover_object_key
            FROM items AS item
            INNER JOIN users AS owner
                ON owner.id = item.owner_id
            LEFT JOIN item_media AS cover
                ON cover.item_id = item.id
                AND cover.sort_order = 0
                AND cover.media_type = 'image'
            ORDER BY item.created_at DESC, item.public_id DESC
            LIMIT :limit
            OFFSET :offset
            """;

    private final DatabaseClient databaseClient;

    public R2dbcPublicListItemReader(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Flux<PublicListedItem> findAllPublicItems(PublicListItemsQuery query) {
        return databaseClient
                .sql(FIND_ALL_PUBLIC_ITEMS)
                .bind("limit", query.fetchLimit())
                .bind("offset", query.offset())
                .map((row, metadata) -> mapItem(row))
                .all();
    }

    private static PublicListedItem mapItem(Row row) {
        var status = requiredColumn(row, "status", String.class);
        var availability = ItemAvailability.valueOf(status.toUpperCase(Locale.ROOT));
        return new PublicListedItem(
                requiredColumn(row, "item_public_id", UUID.class),
                requiredColumn(row, "owner_public_id", UUID.class),
                requiredColumn(row, "owner_username", String.class),
                requiredColumn(row, "name", String.class),
                row.get("description", String.class),
                availability,
                requiredColumn(row, "created_at", OffsetDateTime.class).toInstant(),
                requiredColumn(row, "updated_at", OffsetDateTime.class).toInstant(),
                row.get("cover_object_key", String.class));
    }

    private static <T> T requiredColumn(Row row, String column, Class<T> type) {
        T value = row.get(column, type);
        if (value == null) {
            throw new IllegalStateException("Required public item column was null: " + column);
        }
        return value;
    }
}
