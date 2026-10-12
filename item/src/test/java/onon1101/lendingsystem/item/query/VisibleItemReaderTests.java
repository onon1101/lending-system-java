package onon1101.lendingsystem.item.query;

import static org.assertj.core.api.Assertions.assertThat;

import io.r2dbc.spi.ConnectionFactories;
import java.util.UUID;
import onon1101.lendingsystem.item.list.ListItemsQuery;
import onon1101.lendingsystem.item.list.R2dbcListItemsReader;
import onon1101.lendingsystem.item.retrieve.R2dbcRetrieveItemReader;
import onon1101.lendingsystem.shared.pagination.PageSpec;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.r2dbc.core.DatabaseClient;

class VisibleItemReaderTests {
    @ParameterizedTest
    @CsvSource({
        "available,active", "available,suspended", "available,deleted",
        "unavailable,active", "unavailable,suspended", "unavailable,deleted",
        "archived,active", "archived,suspended", "archived,deleted"
    })
    void listAndDetailApplyTheSameVisibility(String itemStatus, String ownerStatus) {
        var db =
                DatabaseClient.create(
                        ConnectionFactories.get(
                                "r2dbc:h2:mem:///visible_"
                                        + UUID.randomUUID().toString().replace("-", "")
                                        + "?options=DB_CLOSE_DELAY=-1"));
        execute(
                db,
                "CREATE TABLE users (id BIGINT PRIMARY KEY, public_id UUID, username VARCHAR(100), status VARCHAR(20))");
        execute(
                db,
                "CREATE TABLE items (id BIGINT PRIMARY KEY, public_id UUID, owner_id BIGINT, name VARCHAR(100), description VARCHAR(100), status VARCHAR(20), created_at TIMESTAMP WITH TIME ZONE, updated_at TIMESTAMP WITH TIME ZONE)");
        execute(
                db,
                "CREATE TABLE item_media (item_id BIGINT, object_key VARCHAR(100), sort_order INT, media_type VARCHAR(20))");
        db.sql("INSERT INTO users VALUES (7, :id, 'owner', :status)")
                .bind("id", UUID.randomUUID())
                .bind("status", ownerStatus)
                .fetch()
                .rowsUpdated()
                .block();
        var id = UUID.randomUUID();
        db.sql(
                        "INSERT INTO items VALUES (1, :id, 7, 'Item', NULL, :status, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
                .bind("id", id)
                .bind("status", itemStatus)
                .fetch()
                .rowsUpdated()
                .block();
        execute(db, "INSERT INTO item_media VALUES (1, 'gallery', 1, 'image')");
        var shared = new R2dbcVisibleItemReader(db);
        var listReader = new R2dbcListItemsReader(shared);
        var detailReader = new R2dbcRetrieveItemReader(shared);
        for (long viewerId : new long[] {7, 8}) {
            boolean expected =
                    !itemStatus.equals("archived")
                            && (viewerId == 7
                                    || (itemStatus.equals("available")
                                            && ownerStatus.equals("active")));
            var items =
                    listReader
                            .findVisibleItems(viewerId, new ListItemsQuery(new PageSpec(0, 20)))
                            .collectList()
                            .block();
            var detail = detailReader.findVisibleItem(id, viewerId).block();
            assertThat(items).hasSize(expected ? 1 : 0);
            if (expected) {
                assertThat(detail).isNotNull();
                assertThat(detail.itemId()).isEqualTo(items.getFirst().itemId());
                assertThat(detail.ownedByViewer()).isEqualTo(viewerId == 7);
                assertThat(detail.description()).isNull();
                assertThat(detail.coverObjectKey()).isNull();
                assertThat(items.getFirst().coverObjectKey()).isNull();
            } else {
                assertThat(detail).isNull();
            }
        }
        assertThat(detailReader.findVisibleItem(UUID.randomUUID(), 7).block()).isNull();
    }

    private static void execute(DatabaseClient db, String sql) {
        db.sql(sql).fetch().rowsUpdated().block();
    }
}
