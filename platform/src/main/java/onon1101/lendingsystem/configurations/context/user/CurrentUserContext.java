package onon1101.lendingsystem.configurations.context.user;

import java.util.UUID;

public record CurrentUserContext(
        long privateUserId, UUID publicUserId, String username, String email, String status) {

    public boolean active() {
        return "active".equals(status);
    }
}
