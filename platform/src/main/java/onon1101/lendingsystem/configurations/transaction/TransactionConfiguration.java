package onon1101.lendingsystem.configurations.transaction;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalEventPublisher;

/** Enables Reactor-context based transaction interception for Mono and Flux service methods. */
@Configuration
@EnableTransactionManagement(order = Ordered.LOWEST_PRECEDENCE)
public class TransactionConfiguration {

    @Bean
    TransactionalEventPublisher transactionalEventPublisher(
            ApplicationEventPublisher eventPublisher) {
        return new TransactionalEventPublisher(eventPublisher);
    }
}
