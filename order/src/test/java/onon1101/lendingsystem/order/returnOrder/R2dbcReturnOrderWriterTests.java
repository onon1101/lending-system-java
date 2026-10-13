package onon1101.lendingsystem.order.returnOrder;

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

class R2dbcReturnOrderWriterTests {
    private DatabaseClient database;
    private SingleConnectionFactory connectionFactory;
    private R2dbcReturnOrderWriter writer;
    private final Instant now = Instant.parse("2026-10-13T04:00:00Z");

    @BeforeEach
    void setUp() {
        connectionFactory =
                new SingleConnectionFactory("r2dbc:h2:mem:///" + UUID.randomUUID(), true);
        database = DatabaseClient.create(connectionFactory);
        writer = new R2dbcReturnOrderWriter(database);
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
                        "INSERT INTO loan_records (id,item_id,lender_id,borrower_id,status,version) VALUES (30,20,10,11,'borrowed',0)")
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
        assertTrue(writer.returnOrder(30L, 10L, 20L, now).block());
        database.sql("SELECT status, returned_at, version FROM loan_records WHERE id=30")
                .map(
                        (row, metadata) -> {
                            assertEquals("returned", row.get("status", String.class));
                            assertEquals(
                                    now, row.get("returned_at", OffsetDateTime.class).toInstant());
                            assertEquals(1L, row.get("version", Long.class));
                            return true;
                        })
                .one()
                .block();
        assertEquals(
                now,
                database.sql("SELECT closed_at FROM loan_records WHERE id=30")
                        .map(
                                (row, metadata) ->
                                        row.get("closed_at", OffsetDateTime.class).toInstant())
                        .one()
                        .block());
    }

    @Test
    void wrongActorCannotWrite() {
        assertFalse(writer.returnOrder(30L, 99L, 20L, now).block());
    }

    @Test
    void cannotRepeatTransition() {
        assertTrue(writer.returnOrder(30L, 10L, 20L, now).block());
        assertFalse(writer.returnOrder(30L, 10L, 20L, now).block());
    }
}
