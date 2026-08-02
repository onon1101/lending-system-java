package onon1101.lendingsystem.auth.login;

import java.util.UUID;

public record LoginAccount(UUID publicUserId, String username, String passwordHash) {}
