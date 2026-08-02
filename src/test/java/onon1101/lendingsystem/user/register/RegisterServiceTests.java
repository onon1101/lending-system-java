package onon1101.lendingsystem.user.register;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTests {

    private final RegisterAccountWriter accountWriter = mock(RegisterAccountWriter.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final RegistrationAuditLogger auditLogger = mock(RegistrationAuditLogger.class);
    private final RegisterService service =
            new RegisterService(accountWriter, passwordEncoder, auditLogger);

    @AfterEach
    void clearTransactionSynchronization() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void auditsSuccessfulRegistrationOnlyAfterCommit() {
        UUID userId = UUID.randomUUID();
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(accountWriter.registerAccount("alice", "encoded", "alice@example.com"))
                .thenReturn(Optional.of(new RegisterAccount(1L, userId)));
        TransactionSynchronizationManager.initSynchronization();

        service.register(" Alice ", "password", "Alice@example.com");

        verify(auditLogger, never()).registerSuccess("alice", userId);
        List<TransactionSynchronization> synchronizations =
                TransactionSynchronizationManager.getSynchronizations();
        synchronizations.forEach(TransactionSynchronization::afterCommit);
        verify(auditLogger).registerSuccess("alice", userId);
    }

    @Test
    void doesNotAuditSuccessWhenTransactionRollsBack() {
        UUID userId = UUID.randomUUID();
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(accountWriter.registerAccount("alice", "encoded", "alice@example.com"))
                .thenReturn(Optional.of(new RegisterAccount(1L, userId)));
        TransactionSynchronizationManager.initSynchronization();

        service.register("alice", "password", "alice@example.com");
        TransactionSynchronizationManager.getSynchronizations()
                .forEach(
                        synchronization ->
                                synchronization.afterCompletion(
                                        TransactionSynchronization.STATUS_ROLLED_BACK));

        verify(auditLogger, never()).registerSuccess("alice", userId);
    }

    @Test
    void auditsExpectedFailureReason() {
        service.register(" Alice ", "password", "not-an-email");

        verify(auditLogger).registerFailed("alice", "invalid_email");
    }

    @Test
    void auditsUnexpectedFailureAndRethrowsIt() {
        RuntimeException failure = new RuntimeException("encoder unavailable");
        when(passwordEncoder.encode("password")).thenThrow(failure);

        RuntimeException thrown =
                assertThrows(
                        RuntimeException.class,
                        () -> service.register("Alice", "password", "alice@example.com"));

        assertSame(failure, thrown);
        verify(auditLogger).registerFailed("alice", "system_error");
    }
}
