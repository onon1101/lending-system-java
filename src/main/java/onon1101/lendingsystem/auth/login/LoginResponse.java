package onon1101.lendingsystem.auth.login;

public record LoginResponse(String accessToken, String tokenType, long expiresIn) {}
