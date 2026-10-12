package onon1101.lendingsystem.item.list;

public record ListItemsQuery(int page, int size) {

    public ListItemsQuery {
        if (page < 0) {
            throw new IllegalArgumentException("Page must not be negative.");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Size must be between 1 and 100.");
        }
    }

    public long offset() {
        return (long) page * size;
    }

    public int fetchLimit() {
        return size + 1;
    }
}
