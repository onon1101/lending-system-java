package onon1101.lendingsystem.configurations.audit.eventAttributes;

public class UsernameAuditEventAttribute implements AuditEventAttribute {

    private final String value;

    public UsernameAuditEventAttribute(String username) {
        this.value = username;
    }

    @Override
    public String Key() {
        return "accountRef";
    }

    @Override
    public String Value() {
        return value;
    }
}
