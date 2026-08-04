package onon1101.lendingsystem.sharedkernel.context.user;

import java.util.Optional;

public interface CurrentUserReader {

    Optional<CurrentUserContext> findByPrivateId(long privateUserId);
}
