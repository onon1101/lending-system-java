package onon1101.lendingsystem.auth.login;

public record LoginAccount(
        long userId,
        String username,
        String passwordHash) {
}
