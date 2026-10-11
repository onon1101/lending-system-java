package onon1101.lendingsystem.item.uploadCover.exception;

public final class UploadCoverValidationException extends RuntimeException {
    private final String errorCode;

    public UploadCoverValidationException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public String errorCode() {
        return errorCode;
    }
}
