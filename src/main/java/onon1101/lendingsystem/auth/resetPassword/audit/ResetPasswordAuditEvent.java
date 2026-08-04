package onon1101.lendingsystem.auth.resetPassword.audit;

import onon1101.lendingsystem.sharedkernel.audit.AuditEvent;

import onon1101.lendingsystem.sharedkernel.audit.AuditOutcome;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.Objects;

public sealed interface ResetPasswordAuditEvent
        extends AuditEvent {

    String normalizedEmail();

    record Succeeded(String normalizedEmail)
            implements ResetPasswordAuditEvent {

        public Succeeded{
            Objects.requireNonNull(normalizedEmail, "Normalized email must not be null.");
        }

        @Override
        public String eventType() {
            return "password_reset_receiver_successed";
        }

        @Override
        public AuditOutcome outcome() {
            return AuditOutcome.SUCCESS;
        }
    }

    record Failed(String normalizedEmail, String reason) implements ResetPasswordAuditEvent{
        public Failed {
            Objects.requireNonNull(normalizedEmail, "Normalized username must not be null.");
            Objects.requireNonNull(reason, "reason must not be null.");
        }

        @Override
        public String eventType() {
            return "password_reset_receiver_failed";
        }

        @Override
        public AuditOutcome outcome() {
            return AuditOutcome.REJECTED;
        }
    }


}
