package onon1101.lendingsystem.auth.emailVerificationConfirm;

import java.util.UUID;

public interface ValidateEmailWriter {

    boolean updateStateByPublicId(UUID email);
}
