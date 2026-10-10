package onon1101.lendingsystem.item.uploadImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.r2dbc.spi.ConnectionFactories;
import java.time.Instant;
import java.util.UUID;
import onon1101.lendingsystem.item.uploadImage.repository.OwnedItemImage;
import onon1101.lendingsystem.item.uploadImage.repository.R2dbcUploadImageReader;
import onon1101.lendingsystem.item.uploadImage.repository.R2dbcUploadImageWriter;
import org.junit.jupiter.api.Test;
import org.springframework.r2dbc.core.DatabaseClient;

class UploadImageRepositoryTests {
    @Test
    void enforcesOwnershipAndConditionalReplacementIncludingNullKey() {
        var db =
                DatabaseClient.create(
                        ConnectionFactories.get(
                                "r2dbc:h2:mem:///image_"
                                        + UUID.randomUUID().toString().replace("-", "")
                                        + "?options=DB_CLOSE_DELAY=-1"));
        db.sql(
                        """
                CREATE TABLE items (
                    public_id UUID PRIMARY KEY, owner_id BIGINT NOT NULL,
                    status VARCHAR(20) NOT NULL, image_object_key VARCHAR(512),
                    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
                )
                """)
                .fetch()
                .rowsUpdated()
                .block();
        UUID id = UUID.randomUUID();
        db.sql("INSERT INTO items VALUES (:id, 7, 'available', NULL, CURRENT_TIMESTAMP)")
                .bind("id", id)
                .fetch()
                .rowsUpdated()
                .block();
        var reader = new R2dbcUploadImageReader(db);
        var writer = new R2dbcUploadImageWriter(db);
        assertNull(reader.findOwnedItem(id, 8).block());
        assertNotNull(reader.findOwnedItem(id, 7).block());
        var now = Instant.now();
        assertFalse(writer.replace(id, 8, null, "wrong-owner", now).block());
        assertTrue(writer.replace(id, 7, null, "first", now).block());
        assertFalse(writer.replace(id, 7, null, "stale", now).block());
        assertEquals("first", reader.findOwnedItem(id, 7).block().objectKey());
        assertTrue(writer.replace(id, 7, "first", "second", now).block());
        db.sql("UPDATE items SET status = 'archived'").fetch().rowsUpdated().block();
        assertNull(reader.findOwnedItem(id, 7).block());
        assertFalse(writer.replace(id, 7, "second", "third", now).block());
    }
}
