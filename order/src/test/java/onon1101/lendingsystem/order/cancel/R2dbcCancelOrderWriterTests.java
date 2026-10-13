package onon1101.lendingsystem.order.cancel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.r2dbc.connection.SingleConnectionFactory;
import org.springframework.r2dbc.core.DatabaseClient;

class R2dbcCancelOrderWriterTests {
    private DatabaseClient database;
    private SingleConnectionFactory connectionFactory;
    private R2dbcCancelOrderWriter writer;
    private final Instant now = Instant.parse("2026-10-13T04:00:00Z");

    @BeforeEach
    void setUp() {
        connectionFactory =
                new SingleConnectionFactory("r2dbc:h2:mem:///" + UUID.randomUUID(), true);
        database = DatabaseClient.create(connectionFactory);
        writer = new R2dbcCancelOrderWriter(database);
        database.sql(
                        """
                CREATE TABLE loan_records (
                    id BIGINT PRIMARY KEY, item_id BIGINT, lender_id BIGINT, borrower_id BIGINT,
                    status VARCHAR(20) CHECK (status IN ('pending','approved','borrowed','returned','rejected','cancelled')),
                    closed_at TIMESTAMP WITH TIME ZONE, borrowed_at TIMESTAMP WITH TIME ZONE,
                    returned_at TIMESTAMP WITH TIME ZONE, updated_at TIMESTAMP WITH TIME ZONE,
                    version BIGINT
                )
                """)
                .fetch()
                .rowsUpdated()
                .block();
        database.sql(
                        "INSERT INTO loan_records (id,item_id,lender_id,borrower_id,status,version) VALUES (30,20,10,11,'approved',0)")
                .fetch()
                .rowsUpdated()
                .block();
    }

    @AfterEach
    void closeConnection() {
        connectionFactory.destroy();
    }

    @Test
    void writesExpectedStatusTimeAndVersion() {
        assertTrue(writer.cancel(30L, 11L, 20L, now).block());
        database.sql("SELECT status, closed_at, version FROM loan_records WHERE id=30")
                .map(
                        (row, metadata) -> {
                            assertEquals("cancelled", row.get("status", String.class));
                            assertEquals(
                                    now, row.get("closed_at", OffsetDateTime.class).toInstant());
                            assertEquals(1L, row.get("version", Long.class));
                            return true;
                        })
                .one()
                .block();
    }

    @Test
    void wrongActorCannotWrite() {
        assertFalse(writer.cancel(30L, 99L, 20L, now).block());
    }

    @Test
    void cannotRepeatTransition() {
        assertTrue(writer.cancel(30L, 11L, 20L, now).block());
        assertFalse(writer.cancel(30L, 11L, 20L, now).block());
    }
}
