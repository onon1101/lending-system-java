package onon1101.lendingsystem.configurations.Idempotency;

import java.util.UUID;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Repository
public class R2dbcIdempotencyRepository implements IdempotencyRepository {

    private final DatabaseClient databaseClient;
    private final ObjectMapper objectMapper;

    public R2dbcIdempotencyRepository(DatabaseClient databaseClient, ObjectMapper objectMapper) {
        this.databaseClient = databaseClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Boolean> tryAcquire(
            String actorId, String operation, String key, String requestHash) {
        String sql =
                """
            INSERT INTO idempotency_records (
                actor_id,
                operation,
                idempotency_key,
                request_hash,
                status
            )
            VALUES (
                :actorId,
                :operation,
                :key,
                :requestHash,
                'processing'
            )
            ON CONFLICT (actor_id, operation, idempotency_key)
            DO NOTHING
            """;

        return databaseClient
                .sql(sql)
                .bind("actorId", UUID.fromString(actorId))
                .bind("operation", operation)
                .bind("key", key)
                .bind("requestHash", requestHash)
                .fetch()
                .rowsUpdated()
                .map(rows -> rows == 1);
    }

    @Override
    public Mono<IdempotencyRecord> find(String actorId, String operation, String key) {
        String sql =
                """
            SELECT request_hash, status, response_body::text
            FROM idempotency_records
            WHERE actor_id = :actorId
            AND operation = :operation
            AND idempotency_key = :key
            """;

        return databaseClient
                .sql(sql)
                .bind("actorId", UUID.fromString(actorId))
                .bind("operation", operation)
                .bind("key", key)
                .map(
                        (row, metadata) ->
                                new IdempotencyRecord(
                                        require(row.get("request_hash", String.class)),
                                        require(row.get("status", String.class)),
                                        row.get("response_body", String.class)))
                .one();
    }

    @Override
    public Mono<Void> complete(String actorId, String operation, String key, Object response) {
        try {
            String responseJson = objectMapper.writeValueAsString(response);

            return databaseClient
                    .sql(
                            """
              UPDATE idempotency_records
              SET status = 'completed',
                    response_body = CAST(:responseBody AS JSONB),
                    completed_at = CURRENT_TIMESTAMP
                WHERE actor_id = :actorId
                AND operation = :operation
                AND idempotency_key = :key
                AND status = 'processing'
              """)
                    .bind("actorId", UUID.fromString(actorId))
                    .bind("operation", operation)
                    .bind("key", key)
                    .bind("responseBody", responseJson)
                    .fetch()
                    .rowsUpdated()
                    .flatMap(
                            rows ->
                                    rows == 1
                                            ? Mono.<Void>empty()
                                            : Mono.error(
                                                    new IllegalStateException(
                                                            "Failed to complete idempotency record.")));
        } catch (JacksonException exception) {
            return Mono.error(
                    new IllegalStateException(
                            "Failed to serialize idempotent response.", exception));
        }
    }

    private static <T> T require(T value) {
        if (value == null) {
            throw new IllegalStateException("Required idempotency column was null");
        }
        return value;
    }
}
