package onon1101.lendingsystem.order.list;

import io.r2dbc.spi.Row;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Repository
public class R2dbcListOrderReader implements ListOrderReader{

    private static final String SELECT_ORDERS = """
            SELECT
                loan.public_id AS order_public_id,
                item.public_id AS item_public_id,
                loan.item_name_snapshot,
                borrower.public_id AS borrower_public_id,
                borrower.username AS borrower_username,
                lender.public_id AS lender_public_id,
                lender.username AS lender_username,
                loan.status,
                loan.planned_borrow_date,
                loan.planned_return_date,
                loan.requested_at
            FROM loan_records AS loan
            INNER JOIN items AS item ON item.id = loan.item_id
            INNER JOIN users AS borrower ON borrower.id = loan.borrower_id
            INNER JOIN users AS lender ON lender.id = loan.lender_id
            """;

    private static final String ORDER_AND_PAGINATION = """
            ORDER BY loan.requested_at DESC, loan.id DESC
            LIMIT :limit
            OFFSET :offset
            """;

    private final DatabaseClient databaseClient;

    public R2dbcListOrderReader(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Flux<ListedOrder> findForUser(
            long userId,
            ListOrderQuery query
    ) {
        String participantCondition = switch(query.role()) {
            case BORROWER -> " WHERE loan.borrower_id = :userId";
            case LENDER -> " WHERE loan.lender_id = :userId";
        };

        String statusCondition = query.status() == null
                ? ""
                : "AND loan.status = :status";

        String sql = SELECT_ORDERS
                + participantCondition
                + statusCondition
                + ORDER_AND_PAGINATION;

        DatabaseClient.GenericExecuteSpec statement = databaseClient
                .sql(sql)
                .bind("userId", userId)
                .bind("limit", query.pagination().fetchLimit())
                .bind("offset", query.pagination().offset());

        if (query.status() != null) {
            statement = statement.bind("status",  query.status());
        }

        return statement
                .map((row, metadata) -> mapOrder(row))
                .all();
    }

    private static ListedOrder mapOrder(Row row) {
        return new ListedOrder(
                required(row, "order_public_id", UUID.class),
                required(row, "item_public_id", UUID.class),
                required(row, "item_name_snapshot", String.class),
                required(row, "borrower_public_id", UUID.class),
                required(row, "borrower_username", String.class),
                required(row, "lender_public_id", UUID.class),
                required(row, "lender_username", String.class),
                required(row, "status", String.class),
                row.get("planned_borrow_date", LocalDate.class),
                row.get("planned_return_date", LocalDate.class),
                requiredInstant(row, "requested_at")
        );
    }

    private static Instant requiredInstant(Row row, String column) {
        return required(row, column, OffsetDateTime.class).toInstant();
    }

    private static <T> T required(
            Row row,
            String column,
            Class<T> type
    ) {
        return Objects.requireNonNull(
                row.get(column, type),
                "Required order column was null: " + column
        );
    }
}
