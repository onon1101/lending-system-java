package onon1101.lendingsystem.configurations.Idempotency;

public record IdempotencyRecord(String requestHash, String status,
                                String responseBody) {}
