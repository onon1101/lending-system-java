package onon1101.lendingsystem.shared.pagination;
public record PageSpec(int page, int size) {
    public PageSpec {
        if (page < 0) throw new IllegalArgumentException("Page must not be negative.");
        if (size < 1 || size > 100) throw new IllegalArgumentException("Size must be between 1 and 100.");
    }
    public long offset() { return (long) page * size; }
    public int fetchLimit() { return size + 1; }
}
