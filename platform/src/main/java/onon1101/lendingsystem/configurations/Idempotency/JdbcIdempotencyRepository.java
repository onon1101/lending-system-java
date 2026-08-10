package onon1101.lendingsystem.configurations.Idempotency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcIdempotencyRepository implements IdempotencyRepository {

  private final JdbcClient jdbcClient;
  private final ObjectMapper objectMapper;

  public JdbcIdempotencyRepository(JdbcClient jdbcClient,
                                   ObjectMapper objectMapper) {
    this.jdbcClient = jdbcClient;
    this.objectMapper = objectMapper;
  }

  @Override
  public boolean tryAcquire(String actorId, String operation, String key,
                            String requestHash) {
    String sql = """
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

    return jdbcClient.sql(sql)
               .param("actorId", actorId)
               .param("operation", operation)
               .param("key", key)
               .param("requestHash", requestHash)
               .update() == 1;
  }

  @Override
  public Optional<IdempotencyRecord> find(String actorId, String operation,
                                          String key) {
    String sql = """
            SELECT request_hash, status, response_body::text
            FROM idempotency_records
            WHERE actor_id = :actorId
            AND operation = :operation
            AND idempotency_key = :key
            """;

    return jdbcClient.sql(sql)
        .param("actorId", actorId)
        .param("operation", operation)
        .param("key", key)
        .query((resultSet, rowNumber)
                   -> new IdempotencyRecord(resultSet.getString("request_hash"),
                                            resultSet.getString("status"),
                                            resultSet.getString("key")))
        .optional();
  }

  @Override
  public void complete(String actorId, String operation, String key,
                       Object response) {
    try {
      String responseJson = objectMapper.writeValueAsString(response);

      int affectedRows = jdbcClient.sql("""
              UPDATE idempotency_records
              SET status = 'completed',
                    response_body = CAST(:responseBody AS JSONB),
                    completed_at = CURRENT_TIMESTAMP
                WHERE actor_id = :actorId
                AND operation = :operation
                AND idempotecy_key = :key
                AND status = 'processing'
              """)
                             .param("actorId", actorId)
                             .param("operation", operation)
                             .param("key", key)
                             .param("responseBody", responseJson)
                             .update();

      if (affectedRows != 1) {
        throw new IllegalStateException(
            "Failed to complete idempotecy record.");
      }
    } catch (JsonProcessingException exception) {
      throw new IllegalStateException(
          "Failed to serialize idempotent response.");
    }
  }
}
