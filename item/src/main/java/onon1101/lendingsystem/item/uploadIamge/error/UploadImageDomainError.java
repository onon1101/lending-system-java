package onon1101.lendingsystem.item.uploadIamge.error;

import onon1101.lendingsystem.shared.domain.DomainError;

public final class UploadImageDomainError extends DomainError {

    public UploadImageDomainError(String code) {
        super(code, code);
    }
}
