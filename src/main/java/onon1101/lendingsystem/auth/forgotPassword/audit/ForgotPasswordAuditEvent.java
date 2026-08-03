package onon1101.lendingsystem.auth.forgotPassword.audit;

import java.util.Objects;
import onon1101.lendingsystem.sharedkernel.audit.AuditEvent;
import onon1101.lendingsystem.sharedkernel.audit.AuditOutcome;

public sealed interface ForgotPasswordAuditEvent extends AuditEvent {

    String normalizedEmail();

    record Requested(String normalizedEmail) implements ForgotPasswordAuditEvent {

        public Requested {
            Objects.requireNonNull(normalizedEmail, "Normalized email must not be null");
        }

        @Override
        public String eventType() {
            return "password_reset_requested";
        }

        @Override
        public AuditOutcome outcome() {
            return AuditOutcome.SUCCESS;
        }
    }

    record Rejected(String normalizedEmail, String reason) implements ForgotPasswordAuditEvent {

        public Rejected {
            Objects.requireNonNull(normalizedEmail);
            Objects.requireNonNull(reason);
        }

        @Override
        public String eventType() {
            return "password_reset_rejected";
        }

        @Override
        public AuditOutcome outcome() {
            return AuditOutcome.REJECTED;
        }
    }

    record Failed(String normalizedEmail, String reason) implements ForgotPasswordAuditEvent {

        public Failed {
            Objects.requireNonNull(normalizedEmail);
            Objects.requireNonNull(reason);
        }

        @Override
        public String eventType() {
            return "password_reset_failed";
        }

        @Override
        public AuditOutcome outcome() {
            return AuditOutcome.ERROR;
        }
    }
}
