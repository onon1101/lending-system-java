package onon1101.lendingsystem.item.retrieve;

import onon1101.lendingsystem.configurations.context.user.CurrentUserContext;
import onon1101.lendingsystem.configurations.context.user.CurrentUserProvider;

import onon1101.lendingsystem.configurations.domain.Result;

import onon1101.lendingsystem.item.retrieve.error.ItemNotFoundDomainError;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RetrieveItemService {

    private final RetrieveItemReader itemReader;
    private final CurrentUserProvider currentUserProvider;

    public RetrieveItemService(
            RetrieveItemReader itemReader,
            CurrentUserProvider currentUserProvider
    ) {
        this.itemReader = itemReader;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional(readOnly=true)
    public Result<RetrieveItemResult> retrieve(UUID itemId) {
        CurrentUserContext currentUser = currentUserProvider.getCurrentUser();

        RetrievedItem item = itemReader.findVisibleItem(itemId,
                currentUser.privateUserId())
                .orElse(null);

        if(item == null ){
            return Result.failure(new ItemNotFoundDomainError());
        }

        return Result.success(
                RetrieveItemResult.from(item)
        );
    }
}
