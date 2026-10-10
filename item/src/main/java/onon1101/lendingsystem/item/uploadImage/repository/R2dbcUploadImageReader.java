package onon1101.lendingsystem.item.uploadImage.repository;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class R2dbcUploadImageReader implements UploadImageReader {

    private final DatabaseClient databaseClient;

    public R2dbcUploadImageReader(DatabaseClient databaseClient) {
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
                                new OwnedItemImage(row.get("image_object_key",
                                        String.class)))
                .one();
    }
}
