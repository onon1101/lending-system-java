package onon1101.lendingsystem.item.publicList;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;
import onon1101.lendingsystem.item.domain.ItemAvailability;
import onon1101.lendingsystem.web.pagination.PaginationRequest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public class R2dbcPublicListItemReader implements PublicListItemReader {
    private final DatabaseClient databaseClient;

    public R2dbcPublicListItemReader(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Flux<PublicListedItem> findAllPublicItems(PaginationRequest pagination) {
        return databaseClient.sql("""
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
                INNER JOIN users AS owner ON owner.id = item.owner_id
                LEFT JOIN item_media AS cover
                    ON cover.item_id = item.id
                    AND cover.sort_order = 0
                    AND cover.media_type = 'image'
                ORDER BY item.created_at DESC, item.public_id DESC
                LIMIT :limit
                OFFSET :offset
                """)
                .bind("limit", pagination.fetchLimit())
                .bind("offset", pagination.offset())
                .map((row, metadata) -> new PublicListedItem(
                        requirePublicColumn(row.get("item_public_id", UUID.class)),
                        requirePublicColumn(row.get("owner_public_id", UUID.class)),
                        requirePublicColumn(row.get("owner_username", String.class)),
                        requirePublicColumn(row.get("name", String.class)),
                        row.get("description", String.class),
                        ItemAvailability.valueOf(requirePublicColumn(row.get("status", String.class))
                                .toUpperCase(Locale.ROOT)),
                        requirePublicColumn(row.get("created_at", OffsetDateTime.class)).toInstant(),
                        requirePublicColumn(row.get("updated_at", OffsetDateTime.class)).toInstant(),
                        row.get("cover_object_key", String.class)))
                .all();
    }

    private static <T> T requirePublicColumn(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Required public item column was null.");
        }
        return value;
    }
}
