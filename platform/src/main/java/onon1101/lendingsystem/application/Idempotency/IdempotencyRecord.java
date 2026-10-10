package onon1101.lendingsystem.application.idempotency;

public record IdempotencyRecord(String requestHash, String status, String responseBody) {}
