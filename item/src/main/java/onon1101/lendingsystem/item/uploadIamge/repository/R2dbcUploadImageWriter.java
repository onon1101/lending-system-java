package onon1101.lendingsystem.item.uploadIamge.repository;

import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;

@Repository
public class R2dbcUploadImageWriter implements UploadImageWriter {
    private final DatabaseClient databaseClient;
    private final TransactionalOperator transaction;

    public R2dbcUploadImageWriter(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
        this.transaction = TransactionalOperator.create(
                new R2dbcTransactionManager(databaseClient.getConnectionFactory()));
    }

    @Override
    public Mono<SavedImage> append(UUID itemId, long ownerId, String objectKey,
                                   String contentType, Instant now) {
        return lockOwnedItem(itemId, ownerId)
                .flatMap(id -> {
                    return insert(id, objectKey, contentType, now);
                })
                .as(transaction::transactional);
    }

    private Mono<Long> lockOwnedItem(UUID itemId, long ownerId) {
        return databaseClient.sql("""
                        SELECT id FROM items
                        WHERE public_id = :itemId
                          AND owner_id = :ownerId
                          AND status <> 'archived'
                        FOR UPDATE
                        """)
                .bind("itemId", itemId)
                .bind("ownerId", ownerId)
                .map((row, metadata) -> Objects.requireNonNull(
                        row.get("id", Long.class)))
                .one();
    }

    private Mono<SavedImage> insert(long itemId, String objectKey,
                                    String contentType, Instant now) {
        var imageId = UUID.randomUUID();
        var timestamp = now.atOffset(ZoneOffset.UTC);

        return databaseClient.sql("""
                        INSERT INTO item_media (
                            public_id, item_id, media_type,
                            object_key, content_type, sort_order,
                            created_at, updated_at
                        )
                        SELECT :imageId, :itemId, 'image',
                            :objectKey, :contentType,
                            COALESCE(MAX(sort_order), 0) + 1,
                            :now, :now
                        FROM item_media
                        WHERE item_id = :itemId
                        RETURNING public_id, sort_order
                        """)
                .bind("imageId", imageId)
                .bind("itemId", itemId)
                .bind("objectKey", objectKey)
                .bind("contentType", contentType)
                .bind("now", timestamp)
                .map((row, metadata) -> new SavedImage(
                        row.get("public_id", UUID.class),
                        row.get("sort_order", Integer.class)))
                .one()
                .flatMap(saved -> {
                    return updateItemTimestamp(itemId, now).thenReturn(saved);
                });
    }

    private Mono<Void> updateItemTimestamp(long itemId, Instant now) {
        return databaseClient.sql("""
                        UPDATE items
                        SET updated_at = GREATEST(updated_at, :now)
                        WHERE id = :itemId
                        """)
                .bind("itemId", itemId)
                .bind("now", now.atOffset(ZoneOffset.UTC))
                .fetch()
                .rowsUpdated()
                .then();
    }
}
