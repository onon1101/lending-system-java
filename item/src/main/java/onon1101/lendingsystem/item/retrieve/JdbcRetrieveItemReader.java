package onon1101.lendingsystem.item.retrieve;

import onon1101.lendingsystem.item.domain.ItemAvailability;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcRetrieveItemReader implements RetrieveItemReader {

    private final JdbcClient jdbcClient;

    public JdbcRetrieveItemReader(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<RetrievedItem> findVisibleItem(UUID itemId, long viewerId) {
        return jdbcClient
                .sql("""
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
                .param("itemId", itemId)
                .param("viewer", viewerId)
                .query((resultSet, rowNumber) -> new RetrievedItem(
                                resultSet.getObject("item_public_id",
                                        UUID.class),
                                resultSet.getObject("owner_public_id",
                                        UUID.class),
                                resultSet.getString("owner_username"),
                                resultSet.getString("name"),
                                resultSet.getString("description"),
                                ItemAvailability.valueOf(resultSet
                                        .getString("status")
                                        .toUpperCase(
                                                Locale.ROOT)),
                                resultSet.getBoolean("owned_by_viewer"),
                                resultSet
                                        .getObject("created_at",
                                                OffsetDateTime.class)
                                        .toInstant(),
                                resultSet
                                        .getObject("updated_at",
                                                OffsetDateTime.class)
                                        .toInstant()
                        )
                )
                .optional();
    }
}
