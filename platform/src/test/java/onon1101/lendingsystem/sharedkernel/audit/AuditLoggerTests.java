package onon1101.lendingsystem.sharedkernel.audit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import onon1101.lendingsystem.configurations.audit.AccountReferenceEncoder;
import onon1101.lendingsystem.configurations.audit.AuditLogger;
import onon1101.lendingsystem.configurations.audit.AuditRecord;
import onon1101.lendingsystem.configurations.audit.eventAttributes.AuditEventAttribute;
import org.junit.jupiter.api.Test;

class AuditLoggerTests {

    @Test
    void writesAllEventAttributesIntoOneAuditRecord() {
        List<AuditRecord> records = new ArrayList<>();
        AccountReferenceEncoder encoder =
                new AccountReferenceEncoder("test-audit-key-with-at-least-32-characters");
        AuditLogger logger = new AuditLogger(encoder, records::add);
        AuditEventAttribute username = new TestAttribute("usernameRef", "alice");
        AuditEventAttribute item = new TestAttribute("itemRef", "item-123");

        logger.handleSuccess("item_creation_succeeded", List.of(username, item));

        assertThat(records).hasSize(1);
        assertThat(records.get(0).attributes())
                .containsOnlyKeys("usernameRef", "itemRef")
                .doesNotContainValue("alice")
                .doesNotContainValue("item-123");
    }

    private record TestAttribute(String Key, String Value) implements AuditEventAttribute {}
}
