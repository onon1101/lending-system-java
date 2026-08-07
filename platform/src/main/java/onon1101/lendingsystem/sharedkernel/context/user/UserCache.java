package onon1101.lendingsystem.sharedkernel.context.user;

import java.util.Optional;

public interface UserCache {

    Optional<CurrentUserContext> find(long privateUserId);

    void save(CurrentUserContext user);

    void evict(long privateUserId);
}
