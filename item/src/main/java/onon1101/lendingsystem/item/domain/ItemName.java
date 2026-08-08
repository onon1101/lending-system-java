package onon1101.lendingsystem.item.domain;

public record ItemName(String value) {

    private static final int MAX_LENGTH = 100;

    public ItemName {
        if (value == null || value.isBlank()) {
            throw new ItemDomainException(
                    "Item name must not be blank."
            );
        }

        value = value.strip();

        if (value.length() > MAX_LENGTH) {
            throw new ItemDomainException(
                    "Item name must not exceed " + MAX_LENGTH + " characters."
            );
        }
    }

    public static ItemName of(String value) {
        return new ItemName(value);
    }
}
