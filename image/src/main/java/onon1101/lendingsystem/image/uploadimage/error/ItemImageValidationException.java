package onon1101.lendingsystem.image.uploadimage.error;

public final class ItemImageValidationException extends RuntimeException {
    private final String errorCode;

    public ItemImageValidationException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public String errorCode() {
        return errorCode;
    }
}
