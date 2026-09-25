package onon1101.lendingsystem.item.retrieve;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;
import onon1101.lendingsystem.item.domain.ItemAvailability;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcRetrieveItemReader implements RetrieveItemReader {

    private final DatabaseClient databaseClient;

    public R2dbcRetrieveItemReader(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<RetrievedItem> findVisibleItem(UUID itemId, long viewerId) {
        return databaseClient
                .sql(
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
                            item.updated_at
                        FROM items AS item
                        INNER JOIN users AS owner
                            ON owner.id = item.owner_id
                        WHERE item.public_id = :itemId
                          AND item.status <> 'archived'
                          AND (
                                item.owner_id = :viewerId
                                OR (
                                    item.status = 'available'
                                    AND owner.status = 'active'
                                )
                              );
                        """)
                .bind("itemId", itemId)
                .bind("viewerId", viewerId)
                .map(
                        (row, metadata) ->
                                new RetrievedItem(
                                        require(row.get("item_public_id", UUID.class)),
                                        require(row.get("owner_public_id", UUID.class)),
                                        require(row.get("owner_username", String.class)),
                                        require(row.get("name", String.class)),
                                        row.get("description", String.class),
                                        ItemAvailability.valueOf(
                                                require(row.get("status", String.class))
                                                        .toUpperCase(Locale.ROOT)),
                                        Boolean.TRUE.equals(
                                                row.get("owned_by_viewer", Boolean.class)),
                                        require(row.get("created_at", OffsetDateTime.class))
                                                .toInstant(),
                                        require(row.get("updated_at", OffsetDateTime.class))
                                                .toInstant()))
                .one();
    }

    private static <T> T require(T value) {
        if (value == null) {
            throw new IllegalStateException("Required item column was null");
        }
        return value;
    }
}
