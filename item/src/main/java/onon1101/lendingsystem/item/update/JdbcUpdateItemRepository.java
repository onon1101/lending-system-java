package onon1101.lendingsystem.item.update;

import onon1101.lendingsystem.item.domain.Item;

import onon1101.lendingsystem.item.domain.ItemAvailability;
import onon1101.lendingsystem.item.domain.ItemDescription;
import onon1101.lendingsystem.item.domain.ItemId;

import onon1101.lendingsystem.item.domain.ItemName;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcUpdateItemRepository implements UpdateItemReader, UpdateItemWriter {

    private final JdbcClient jdbcClient;

    public JdbcUpdateItemRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<Item> finOwnedItem(UUID itemId, long ownerId) {
        return jdbcClient
                .sql("""
                        SELECT
                        	public_id,
                        	owner_id,
                        	name,
                        	description,
                        	status,
                        	created_at,
                        	updated_at
                        FROM items
                        WHERE public_id = :publicId
                        	AND owner_id = :ownerId
                        	AND status <> 'archived';
                        """)
                .param("publicId", itemId)
                .param("ownerId", ownerId)
                .query((resultSet, rowNumber) -> Item.reconstitute(
                        ItemId.of(resultSet.getObject("public_id", UUID.class)),
                        resultSet.getLong("owner_id"),
                        ItemName.of(resultSet.getString("name")),
                        ItemDescription.of(resultSet.getString("description")),
                        ItemAvailability.valueOf(resultSet.getString("status")),
                        resultSet
                                .getTimestamp("created_at")
                                .toInstant(),
                        resultSet
                                .getTimestamp("updated_at")
                                .toInstant()
                ))
                .optional();
    }

    @Override
    public boolean update(Item item) {
        int affectedRow =
                jdbcClient
                        .sql("""
                                UPDATE items
                                SET name = :name,
                                    description = :description,
                                    updated_at = :updatedAt
                                WHERE public_id = :itemId
                                AND owner_id = :ownerId
                                AND status <> 'archived'
                                """)
                        .param("name", item.name().value())
                        .param("description", item.description().value())
                        .param("updatedAt", item.updatedAt().atOffset(
                                ZoneOffset.UTC))
                        .param("itemId", item.id().value())
                        .param("ownerId", item.ownerId())
                        .update();

        return affectedRow == 1;
    }
}
