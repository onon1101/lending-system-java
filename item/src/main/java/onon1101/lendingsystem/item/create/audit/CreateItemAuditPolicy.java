package onon1101.lendingsystem.item.create.audit;

import java.util.List;
import onon1101.lendingsystem.configurations.audit.AuditEvent;
import onon1101.lendingsystem.configurations.audit.CommandAuditPolicy;
import onon1101.lendingsystem.configurations.audit.eventAttributes.ItemAuditEventAttribute;
import onon1101.lendingsystem.configurations.audit.eventAttributes.ItemCreationAuditEventAttribute;
import onon1101.lendingsystem.item.create.CreateItemCommand;
import onon1101.lendingsystem.item.create.CreateItemResult;
import org.springframework.stereotype.Component;

@Component
public final class CreateItemAuditPolicy
        implements CommandAuditPolicy<CreateItemCommand, CreateItemResult, AuditEvent> {

    @Override
    public AuditEvent onReturned(CreateItemCommand command, CreateItemResult result) {
        return new AuditEvent.Success(
                "item_creation_succeeded",
                List.of(new ItemAuditEventAttribute(result.itemId().toString())));
    }

    @Override
    public AuditEvent onThrown(CreateItemCommand command, Throwable throwable) {
        return new AuditEvent.Failed(
                "item_creation_failed",
                List.of(new ItemCreationAuditEventAttribute(command.name())),
                "system_error");
    }
}
