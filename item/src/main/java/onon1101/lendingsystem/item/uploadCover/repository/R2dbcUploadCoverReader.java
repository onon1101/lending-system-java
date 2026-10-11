package onon1101.lendingsystem.item.uploadCover.repository;

import java.util.UUID;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcUploadCoverReader implements UploadCoverReader {

    private final DatabaseClient databaseClient;

    public R2dbcUploadCoverReader(DatabaseClient databaseClient) {
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
}
