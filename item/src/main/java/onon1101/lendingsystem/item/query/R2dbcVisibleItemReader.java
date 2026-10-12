package onon1101.lendingsystem.item.query;

import io.r2dbc.spi.Row;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;
import onon1101.lendingsystem.item.domain.ItemAvailability;
import onon1101.lendingsystem.shared.pagination.PageSpec;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcVisibleItemReader {
    private static final String VISIBLE_ITEMS_SQL =
            """
                        SELECT
                            item.public_id AS item_public_id,
                            owner.public_id AS owner_public_id,
                            owner.username AS owner_username,
                            item.name,
                            item.description,
                            item.status,
                            (item.owner_id = :viewerId) AS owned_by_viewer,
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
                        WHERE item.status <> 'archived'
                          AND (
                                item.owner_id = :viewerId
                                OR (
                                    item.status = 'available'
                                    AND owner.status = 'active'
                                )
                              )
                        """;
    private static final String LIST_SQL =
            VISIBLE_ITEMS_SQL
                    + """
            ORDER BY item.created_at DESC, item.public_id DESC
            LIMIT :limit
            OFFSET :offset
            """;
    private static final String DETAIL_SQL =
            VISIBLE_ITEMS_SQL
                    + """
            AND item.public_id = :itemId
            """;
    private final DatabaseClient databaseClient;

    public R2dbcVisibleItemReader(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Flux<VisibleItem> findVisibleItems(long viewerId, PageSpec pagination) {
        return databaseClient
                .sql(LIST_SQL)
                .bind("viewerId", viewerId)
                .bind("limit", pagination.fetchLimit())
                .bind("offset", pagination.offset())
                .map((row, metadata) -> mapItem(row))
                .all();
    }

    public Mono<VisibleItem> findVisibleItem(UUID itemId, long viewerId) {
        return databaseClient
                .sql(DETAIL_SQL)
                .bind("viewerId", viewerId)
                .bind("itemId", itemId)
                .map((row, metadata) -> mapItem(row))
                .one();
    }

    private static VisibleItem mapItem(Row row) {
        return new VisibleItem(
                requiredColumn(row, "item_public_id", UUID.class),
                requiredColumn(row, "owner_public_id", UUID.class),
                requiredColumn(row, "owner_username", String.class),
                requiredColumn(row, "name", String.class),
                row.get("description", String.class),
                ItemAvailability.valueOf(
                        requiredColumn(row, "status", String.class).toUpperCase(Locale.ROOT)),
                requiredColumn(row, "owned_by_viewer", Boolean.class),
                requiredColumn(row, "created_at", OffsetDateTime.class).toInstant(),
                requiredColumn(row, "updated_at", OffsetDateTime.class).toInstant(),
                row.get("cover_object_key", String.class));
    }

    private static <T> T requiredColumn(Row row, String column, Class<T> type) {
        T value = row.get(column, type);
        if (value == null) {
            throw new IllegalStateException("Required item column was null: " + column);
        }
        return value;
    }
}
