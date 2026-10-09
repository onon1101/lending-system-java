package onon1101.lendingsystem.item.list;

import onon1101.lendingsystem.configurations.pagination.PaginationRequest;

import onon1101.lendingsystem.item.domain.ItemAvailability;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;

@Repository
public class R2dbcListItemsReader implements ListItemsReader {

    private final DatabaseClient databaseClient;

    public R2dbcListItemsReader(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Flux<ListedItem> findVisibleItems(
            long viewerId,
            PaginationRequest pagination
    ) {
        return databaseClient
                .sql(
                        """
                         SELECT
                                 	item.public_id AS item_public_id
                                 	owner.public_id AS owner_public_id
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
                                 WHERE item.status <> 'archived'
                                 	AND (
                                 		item.owner_id = :viewerId
                                 		OR (
                                 			item.status = 'available'
                                 			AND owner.status = 'active'
                                 		)
                                 	)
                                 ORDER BY item.created_at DESC, item.public_id DESC
                                 LIMIT :limit
                                 OFFSET :offset
                        """
                )
                .bind("viewerId", viewerId)
                .bind("limit", pagination.fetchLimit())
                .bind("offset", pagination.offset())
                .map(
                        (row, metadata) ->
                                new ListedItem(
                                        require(row.get("item_public_id", UUID.class)),
                                        require(row.get("owner_public_id", UUID.class)),
                                        require(row.get("owner_username", String.class)),
                                        require(row.get("name", String.class)),
                                        row.get("description", String.class),
                                        ItemAvailability.valueOf(
                                                require(row.get("status", String.class))
                                                        .toUpperCase(Locale.ROOT)
                                        ),
                                        Boolean.TRUE.equals(row.get("ownered_by_viewer", Boolean.class)),
                                        require(row.get("created_at", OffsetDateTime.class)).toInstant(),
                                        require(row.get("updated_at", OffsetDateTime.class)).toInstant()
                                )
                )
                .all();
    }

    private static <T> T require(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Required item column was null.");
        }

        return value;
    }
}
