package onon1101.lendingsystem.item.uploadIamge.repository;

import java.util.UUID;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcUploadImageReader implements UploadImageReader {
    private final DatabaseClient databaseClient;

    public R2dbcUploadImageReader(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Boolean> isOwnedItem(UUID itemId, long ownerId) {
        return databaseClient
                .sql(
                        """
                        SELECT id FROM items
                        WHERE public_id = :itemId
                          AND owner_id = :ownerId
                          AND status <> 'archived'
                        """)
                .bind("itemId", itemId)
                .bind("ownerId", ownerId)
                .map((row, metadata) -> row.get("id", Long.class))
                .one()
                .hasElement();
    }
}
