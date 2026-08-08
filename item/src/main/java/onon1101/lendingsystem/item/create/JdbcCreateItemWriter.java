package onon1101.lendingsystem.item.create;

import onon1101.lendingsystem.item.domain.Item;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.ZoneOffset;
import java.util.Locale;

@Repository
public class JdbcCreateItemWriter implements CreateItemWriter {

    private final JdbcClient jdbcClient;

    public JdbcCreateItemWriter(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void create(Item item) {
        String sql = """
                INSERT INTO items (
                    public_id,
                    owner_id,
                    name,
                    description,
                    status,
                    created_at,
                    updated_at
                )
                VALUES (
                    :publicId,
                    :ownerId,
                    :name,
                    :description,
                    :status,
                    :createdAt,
                    :updatedAt
                )
                """;

        int affectedRows =
                jdbcClient.sql(sql)
                        .param("publicId", item.id().value())
                        .param("ownerId", item.ownerId())
                        .param("name", item.name().value())
                        .param("description", item.description().value())
                        .param("status", item.availability().name().toLowerCase(
                                Locale.ROOT))
                        .param("createdAt", item.createdAt().atOffset(
                                ZoneOffset.UTC))
                        .param("updatedAt", item.updatedAt().atOffset(
                                ZoneOffset.UTC
                        ))
                        .update();

        if (affectedRows != 1) {
            throw new IllegalStateException(
                    "Expected to create one item, but affected " +  affectedRows + " rows."
            );
        }
    }
}
