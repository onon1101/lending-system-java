package onon1101.lendingsystem.item.delete;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

@Repository
public class JdbcDeleteItemWriter implements DeleteItemWriter {

    private final JdbcClient jdbcClient;

    public JdbcDeleteItemWriter(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public boolean archiveOwnedItem(
            UUID itemId,
            long ownerId,
            Instant archivedAt
    ) {
        int affectedRows =
                jdbcClient
                        .sql("""
                                UPDATE items
                                SET status = 'archived',
                                    updated_at = :updatedAt
                                WHERE public_id = :publicId
                                AND owner_id = :ownerId
                                AND status <> 'archived'
                                """)
                        .param("itemId", itemId)
                        .param("ownerId", ownerId)
                        .param("updatedAt", archivedAt.atOffset(ZoneOffset.UTC))
                        .update();

        return affectedRows == 1;
    }
}
