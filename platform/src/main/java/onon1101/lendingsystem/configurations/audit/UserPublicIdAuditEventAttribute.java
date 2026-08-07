package onon1101.lendingsystem.configurations.audit;

import java.util.UUID;

public class UserPublicIdAuditEventAttribute implements AuditEventAttribute {

    public UserPublicIdAuditEventAttribute(UUID publicUserId) {
        this.value = publicUserId.toString();
    }

    private final String value;

    @Override
    public String Key() {
        return "publicUserIdRef";
    }

    @Override
    public String Value() {
        return value;
    }
}
