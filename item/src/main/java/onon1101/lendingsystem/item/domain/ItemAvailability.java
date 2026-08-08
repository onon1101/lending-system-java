package onon1101.lendingsystem.item.domain;

/**
 * 是否允許別人提出借用申請。
 */
public enum ItemAvailability {
    AVAILABLE,
    UNAVAILABLE,
    ARCHIVED;

    public boolean canReceiveBorrowRequest() {
        return this == AVAILABLE;
    }
}
