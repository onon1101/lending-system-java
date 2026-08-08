package onon1101.lendingsystem.item.domain;

public record ItemDescription(String value) {

    private static final int MAX_LENGTH = 2_000;

    public ItemDescription {
        value = value == null ? "" : value.strip();

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "Item description must not exceed " + MAX_LENGTH + " characters"
            );
        }
    }

    public static ItemDescription of(String value) {
        return new ItemDescription(value);
    }

    public static ItemDescription empty() {
        return new ItemDescription("");
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }
}
