package onon1101.lendingsystem.infrastructure.image;

public final class ImageValidationException extends RuntimeException {
    private final String errorCode;

    public ImageValidationException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public String errorCode() {
        return errorCode;
    }
}
