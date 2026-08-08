package onon1101.lendingsystem.configurations.transaction;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** Ensures result inspection runs inside the transaction interceptor. */
@Configuration
@EnableTransactionManagement(order = Ordered.LOWEST_PRECEDENCE - 1)
public class TransactionConfiguration {}
