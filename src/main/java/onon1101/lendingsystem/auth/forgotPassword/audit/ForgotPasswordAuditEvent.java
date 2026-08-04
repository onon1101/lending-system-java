package onon1101.lendingsystem.auth.forgotPassword.audit;

import java.util.Objects;
import onon1101.lendingsystem.sharedkernel.audit.AuditEvent;

public sealed interface ForgotPasswordAuditEvent extends AuditEvent {

    record Requested(String normalizedEmail) implements ForgotPasswordAuditEvent {

        public Requested {
            Objects.requireNonNull(normalizedEmail, "Normalized email must not be null");
        }

    }

    record Rejected(String normalizedEmail, String reason) implements ForgotPasswordAuditEvent {

        public Rejected {
            Objects.requireNonNull(normalizedEmail);
            Objects.requireNonNull(reason);
        }

    }

    record Failed(String normalizedEmail, String reason) implements ForgotPasswordAuditEvent {

        public Failed {
            Objects.requireNonNull(normalizedEmail);
            Objects.requireNonNull(reason);
        }

    }
}
