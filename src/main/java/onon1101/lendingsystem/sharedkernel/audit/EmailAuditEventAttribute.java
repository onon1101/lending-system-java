package onon1101.lendingsystem.sharedkernel.audit;

public class EmailAuditEventAttribute implements AuditEventAttribute {

    public EmailAuditEventAttribute(String email) {
        this.value = email;
    }

    private final String value;

    @Override
    public String Key() {
        return "emailRef";
    }

    @Override
    public String Value() {
        return value;
    }
}
