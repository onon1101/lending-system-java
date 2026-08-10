package onon1101.lendingsystem.configurations.Idempotency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;

@Service
public class IdempotencyService {

  private final JdbcIdempotencyRepository repository;
  private final ObjectMapper objectMapper;

  public IdempotencyService(JdbcIdempotencyRepository repository,
                            ObjectMapper objectMapper) {
    this.repository = repository;
    this.objectMapper = objectMapper;
  }

  public <T> T execute(String actorId, String operation, String key,
                       Object request, Class<T> responseType,
                       Supplier<T> action) {

    String requestHash = hash(request);

    if (repository.tryAcquire(actorId, operation, key, requestHash)) {
      T response = action.get();
      repository.complete(actorId, operation, key, response);
      return response;
    }

    IdempotencyRecord existing =
        repository.find(actorId, operation, key)
            .orElseThrow(()
                             -> new IllegalStateException(
                                 "Idempotecy record disappeared."));

    if (!existing.requestHash().equals(requestHash)) {
      throw new IdempotencyConfliectException();
    }

    if (!"completed".equals(existing.status())) {
      throw new IdempotencyInProgressException();
    }

    try {
      return objectMapper.readValue(existing.responseBody(), responseType);
    } catch (JsonProcessingException exception) {
      throw new IllegalStateException(
          "Failed to deserialize idempotent response", exception);
    }
  }

  /**
   * 客戶端請求哈希化
   * @param request Client Request
   */
  private String hash(Object request) {
    try {
      byte[] serialized = objectMapper.writeValueAsBytes(request);

      return HexFormat.of().formatHex(
          MessageDigest.getInstance("SHA-256").digest(serialized));
    } catch (JsonProcessingException | NoSuchAlgorithmException exception) {
      throw new IllegalStateException("Failed to hash idempotency request.");
    }
  }
}
