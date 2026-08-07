package onon1101.lendingsystem.configurations.context.user;

import java.util.Optional;

public interface CurrentUserReader {

    Optional<CurrentUserContext> findByPrivateId(long privateUserId);
}
