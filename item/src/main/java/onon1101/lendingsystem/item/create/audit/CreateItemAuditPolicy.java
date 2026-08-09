package onon1101.lendingsystem.item.create.audit;

import onon1101.lendingsystem.configurations.audit.AuditEvent;
import onon1101.lendingsystem.configurations.audit.CommandAuditPolicy;

import onon1101.lendingsystem.configurations.audit.eventAttributes.ItemAuditEventAttribute;
import onon1101.lendingsystem.configurations.audit.eventAttributes.ItemCreationAuditEventAttribute;
import onon1101.lendingsystem.configurations.context.user.CurrentUserContext;
import onon1101.lendingsystem.configurations.context.user.CurrentUserProvider;
import onon1101.lendingsystem.configurations.domain.Result;
import onon1101.lendingsystem.item.create.CreateItemCommand;

import onon1101.lendingsystem.item.create.CreateItemResult;

import org.springframework.stereotype.Component;

@Component
public final class CreateItemAuditPolicy
        implements CommandAuditPolicy<CreateItemCommand, CreateItemResult,
        AuditEvent> {

    private final CurrentUserProvider currentUserProvider;

    public CreateItemAuditPolicy(
            CurrentUserProvider currentUserProvider
    ) {
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public AuditEvent onReturned(
            CreateItemCommand command,
            CreateItemResult result
    ) {
        CurrentUserContext currentUser = currentUserProvider.getCurrentUser();

        return new AuditEvent.Success("item_creation_succeeded",
                //todo: 同時有 userid, itemid 的部分。
                new ItemAuditEventAttribute(result
                        .itemId()
                        .toString()));
    }

    @Override
    public AuditEvent onThrown(
            CreateItemCommand command,
            Throwable throwable
    ) {
        return new AuditEvent.Failed(
                "item_creation_failed",
                new ItemCreationAuditEventAttribute(command.name()),
                "system_error"
        );
    }
}
