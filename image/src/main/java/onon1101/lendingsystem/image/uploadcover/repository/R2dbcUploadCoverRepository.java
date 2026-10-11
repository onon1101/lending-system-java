package onon1101.lendingsystem.image.uploadcover.repository;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import onon1101.lendingsystem.image.uploadcover.OwnedItemCover;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcUploadCoverRepository
        implements UploadCoverReader, UploadCoverWriter {

    private final DatabaseClient databaseClient;

    public R2dbcUploadCoverRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<OwnedItemCover> findOwnedItem(UUID itemId, long ownerId) {
        return databaseClient
                .sql(
                        """
                        SELECT cover.object_key AS cover_object_key
                        FROM items AS item
                        LEFT JOIN item_media AS cover
                            ON cover.item_id = item.id
                            AND cover.sort_order = 0
                            AND cover.media_type = 'image'
                        WHERE item.public_id = :itemId
                        AND item.owner_id = :ownerId
                        AND item.status <> 'archived'
                        """)
                .bind("itemId", itemId)
                .bind("ownerId", ownerId)
                .map(
                        (row, metadata) ->
                                new OwnedItemCover(row.get("cover_object_key", String.class)))
                .one();
    }

    @Override
    public Mono<Boolean> replace(
            UUID itemId, long ownerId, String expectedKey, String newKey, Instant updatedAt) {
        var transaction =
                TransactionalOperator.create(
                        new R2dbcTransactionManager(databaseClient.getConnectionFactory()));

        // Serialize changes per item, including the first upload when no media row exists.
        return databaseClient
                .sql(
                        """
                        SELECT id FROM items
                        WHERE public_id = :itemId AND owner_id = :ownerId
                          AND status <> 'archived'
                        FOR UPDATE
                        """)
                .bind("itemId", itemId)
                .bind("ownerId", ownerId)
                .map((row, metadata) -> row.get("id", Long.class))
                .one()
                .flatMap(id -> replaceCover(id, expectedKey, newKey, updatedAt))
                .defaultIfEmpty(false)
                .as(transaction::transactional);
    }

    private Mono<Boolean> replaceCover(
            long id, String expectedKey, String newKey, Instant updatedAt) {
        return databaseClient
                .sql(
                        """
                        SELECT object_key FROM item_media
                        WHERE item_id = :id AND sort_order = 0 AND media_type = 'image'
                        """)
                .bind("id", id)
                .map((row, metadata) -> Optional.of(row.get("object_key", String.class)))
                .one()
                .defaultIfEmpty(Optional.empty())
                .flatMap(
                        existing -> {
                            if (!Objects.equals(existing.orElse(null), expectedKey)) {
                                return Mono.just(false);
                            }

                            var query =
                                    expectedKey == null
                                            ? databaseClient
                                                    .sql(
                                                            """
                                    INSERT INTO item_media
                                        (public_id, item_id, media_type, object_key, sort_order,
                                         created_at, updated_at)
                                    VALUES (:mediaId, :id, 'image', :newKey, 0,
                                            :updatedAt, :updatedAt)
                                    """)
                                                    .bind("mediaId", UUID.randomUUID())
                                            : databaseClient
                                                    .sql(
                                                            """
                                    UPDATE item_media
                                    SET object_key = :newKey,
                                        updated_at = GREATEST(updated_at, :updatedAt)
                                    WHERE item_id = :id AND sort_order = 0
                                      AND media_type = 'image' AND object_key = :expectedKey
                                    """)
                                                    .bind("expectedKey", expectedKey);

                            return query.bind("id", id)
                                    .bind("newKey", newKey)
                                    .bind("updatedAt", updatedAt.atOffset(ZoneOffset.UTC))
                                    .fetch()
                                    .rowsUpdated()
                                    .flatMap(
                                            rows -> {
                                                if (rows != 1) {
                                                    return Mono.just(false);
                                                }
                                                return databaseClient
                                                        .sql(
                                                                """
                                                UPDATE items
                                                SET updated_at = GREATEST(updated_at, :updatedAt)
                                                WHERE id = :id
                                                """)
                                                        .bind("id", id)
                                                        .bind(
                                                                "updatedAt",
                                                                updatedAt.atOffset(ZoneOffset.UTC))
                                                        .fetch()
                                                        .rowsUpdated()
                                                        .thenReturn(true);
                                            });
                        });
    }
}
