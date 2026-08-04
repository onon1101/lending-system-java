package onon1101.lendingsystem.user.validateEmail;

import java.util.UUID;

public interface ValidateEmailWriter {

    boolean updateStateByPublicId(UUID email);
}
