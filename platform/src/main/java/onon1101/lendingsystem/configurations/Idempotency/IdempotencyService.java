package onon1101.lendingsystem.configurations.Idempotency;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
public class IdempotencyService {

    private final IdempotencyRepository repository;
    private final ObjectMapper objectMapper;

    public IdempotencyService(IdempotencyRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public <T> Mono<T> execute(
            String actorId,
            String operation,
            String key,
            Object request,
            Class<T> responseType,
            Supplier<Mono<T>> action) {

        String requestHash = hash(request);

        return repository
                .tryAcquire(actorId, operation, key, requestHash)
                .flatMap(
                        acquired ->
                                acquired
                                        ? action.get()
                                                .flatMap(
                                                        response ->
                                                                repository
                                                                        .complete(
                                                                                actorId, operation,
                                                                                key, response)
                                                                        .thenReturn(response))
                                        : replay(
                                                actorId,
                                                operation,
                                                key,
                                                requestHash,
                                                responseType));
    }

    private <T> Mono<T> replay(
            String actorId,
            String operation,
            String key,
            String requestHash,
            Class<T> responseType) {
        return repository
                .find(actorId, operation, key)
                .switchIfEmpty(
                        Mono.error(new IllegalStateException("Idempotency record disappeared.")))
                .flatMap(
                        existing -> {
                            if (!existing.requestHash().equals(requestHash)) {
                                return Mono.error(new IdempotencyConfliectException());
                            }
                            if (!"completed".equals(existing.status())) {
                                return Mono.error(new IdempotencyInProgressException());
                            }
                            try {
                                return Mono.just(
                                        objectMapper.readValue(
                                                existing.responseBody(), responseType));
                            } catch (JacksonException exception) {
                                return Mono.error(
                                        new IllegalStateException(
                                                "Failed to deserialize idempotent response",
                                                exception));
                            }
                        });
    }

    /**
     * 客戶端請求哈希化
     *
     * @param request Client Request
     */
    private String hash(Object request) {
        try {
            byte[] serialized = objectMapper.writeValueAsBytes(request);

            return HexFormat.of()
                    .formatHex(MessageDigest.getInstance("SHA-256").digest(serialized));
        } catch (JacksonException | NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Failed to hash idempotency request.");
        }
    }
}
