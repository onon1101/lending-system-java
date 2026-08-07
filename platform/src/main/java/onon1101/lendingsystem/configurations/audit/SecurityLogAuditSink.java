package onon1101.lendingsystem.configurations.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Writes audit records to the dedicated security audit logger. */
@Component
public class SecurityLogAuditSink implements AuditSink {

    private static final Logger AUDIT_LOGGER = LoggerFactory.getLogger("SECURITY_AUDIT");

    @Override
    public void append(AuditRecord record) {
        String message = "event={} outcome={} attributes={}";
        switch (record.outcome()) {
            case SUCCESS ->
                    AUDIT_LOGGER.info(
                            message, record.eventType(), record.outcome(), record.attributes());
            case REJECTED ->
                    AUDIT_LOGGER.warn(
                            message, record.eventType(), record.outcome(), record.attributes());
            case ERROR ->
                    AUDIT_LOGGER.error(
                            message, record.eventType(), record.outcome(), record.attributes());
        }
    }
}
