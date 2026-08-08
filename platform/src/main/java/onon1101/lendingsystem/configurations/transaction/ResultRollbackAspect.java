package onon1101.lendingsystem.configurations.transaction;

import onon1101.lendingsystem.configurations.domain.Result;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** Marks the current transaction as rollback-only when a command returns a failed result. */
@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class ResultRollbackAspect {

    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object rollbackOnFailure(ProceedingJoinPoint joinPoint) throws Throwable {
        Object returnedValue = joinPoint.proceed();

        if (returnedValue instanceof Result<?> result && result.isFailure()) {
            if (!TransactionSynchronizationManager.isActualTransactionActive()) {
                throw new IllegalStateException(
                        "Result.Failure was returned without an active transaction: "
                                + joinPoint.getSignature().toLongString());
            }

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return returnedValue;
    }
}
