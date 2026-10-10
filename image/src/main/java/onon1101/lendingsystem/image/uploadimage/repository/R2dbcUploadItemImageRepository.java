package onon1101.lendingsystem.image.uploadimage.repository;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import onon1101.lendingsystem.image.uploadimage.OwnedItemImage;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcUploadItemImageRepository
        implements UploadItemImageReader, UploadItemImageWriter {

    private final DatabaseClient databaseClient;

    public R2dbcUploadItemImageRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<OwnedItemImage> findOwnedItem(UUID itemId, long ownerId) {
        return databaseClient
                .sql(
                        """
                        SELECT image_object_key
                        FROM items
                        WHERE public_id = :itemId
                        AND owner_id = :ownerId
                        AND status <> 'archived'
                        """)
                .bind("itemId", itemId)
                .bind("ownerId", ownerId)
                .map(
                        (row, metadata) ->
                                new OwnedItemImage(row.get("image_object_key", String.class)))
                .one();
    }

    @Override
    public Mono<Boolean> replace(
            UUID itemId, long ownerId, String expectedKey, String newKey, Instant updatedAt) {
        var query =
                databaseClient
                        .sql(
                                """
                UPDATE items
                SET image_object_key = :newKey,
                updated_at = GREATEST(updated_at, :updatedAt)
                WHERE public_id = :itemId
                AND owner_id = :ownerId
                AND status <> 'archived'
                AND image_object_key IS NOT DISTINCT FROM :expectedKey
                """)
                        .bind("itemId", itemId)
                        .bind("ownerId", ownerId)
                        .bind("newKey", newKey)
                        .bind("updatedAt", updatedAt.atOffset(ZoneOffset.UTC));

        query =
                expectedKey == null
                        ? query.bindNull("expectedKey", String.class)
                        : query.bind("expectedKey", expectedKey);

        return query.fetch().rowsUpdated().map(rows -> rows == 1);
    }
}
