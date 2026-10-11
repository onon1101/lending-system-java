package onon1101.lendingsystem.image.uploadcover.error;

public final class ItemCoverValidationException extends RuntimeException {
    private final String errorCode;

    public ItemCoverValidationException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public String errorCode() {
        return errorCode;
    }
}
