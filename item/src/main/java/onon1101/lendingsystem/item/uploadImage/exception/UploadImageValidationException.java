package onon1101.lendingsystem.item.uploadImage.exception;

public final class UploadImageValidationException extends RuntimeException {
    private final String errorCode;

    public UploadImageValidationException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public String errorCode() {
        return errorCode;
    }
}
