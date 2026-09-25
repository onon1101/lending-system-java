package onon1101.lendingsystem.item.delete;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcDeleteItemWriter implements DeleteItemWriter {

    private final DatabaseClient databaseClient;

    public R2dbcDeleteItemWriter(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Boolean> archiveOwnedItem(UUID itemId, long ownerId, Instant archivedAt) {
        return databaseClient
                .sql(
                        """
                                UPDATE items
                                SET status = 'archived',
                                    updated_at = :updatedAt
                                WHERE public_id = :publicId
                                AND owner_id = :ownerId
                                AND status <> 'archived'
                                """)
                .bind("publicId", itemId)
                .bind("ownerId", ownerId)
                .bind("updatedAt", archivedAt.atOffset(ZoneOffset.UTC))
                .fetch()
                .rowsUpdated()
                .map(rows -> rows == 1);
    }
}
