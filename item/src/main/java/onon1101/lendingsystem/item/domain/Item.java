package onon1101.lendingsystem.item.domain;

import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;

/**
 * 物品 domain model
 */
public final class Item {

    private final ItemId id;
    private final long ownerId;
    private final Instant createdAt;

    private ItemName name;
    private ItemDescription description;
    private ItemAvailability availability;
    private Instant updatedAt;

    /**
     * 建構子。
     *
     * @param id 物品 ID
     * @param ownerId 擁有者 ID
     * @param name 物品名稱
     * @param description 物品描述
     * @param availability 可用性
     * @param createdAt 建立於
     * @param updatedAt 更改於
     */
    private Item(
            ItemId id,
            long ownerId,
            ItemName name,
            ItemDescription description,
            ItemAvailability availability,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "Item ID must not be null.");

        if (ownerId <= 0) {
            throw new IllegalArgumentException("Owner ID must be greater than zero.");
        }

        this.ownerId = ownerId;
        this.name = Objects.requireNonNull(name, "Item name must not be null.");
        this.description = Objects.requireNonNull(description, "Item Description must not be null.");
        this.availability = Objects.requireNonNull(availability, "Item Availability must not be null.");
        this.createdAt = Objects.requireNonNull(createdAt, "Item CreatedAt must not be null.");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Item UpdatedAt must not be null.");

        if (updatedAt.isBefore(createdAt)) {
            throw new ItemDomainException("Updated time must not be before created time.");
        }
    }

    /**
     * 建立 Item domain model。
     *
     * @param id 物品 ID
     * @param ownerId 擁有者 ID
     * @param name 物品名稱
     * @param description 物品描述
     * @param now 建立於
     */
    public static Item create(
            ItemId id,
            long ownerId,
            ItemName name,
            ItemDescription description,
            Instant now) {
        return new Item(
                id,
                ownerId,
                name,
                description,
                ItemAvailability.AVAILABLE,
                now,
                now);
    }

    /**
     * 用資料庫 Item domain model。
     *
     * @param id 物品 ID
     * @param ownerId 擁有者 ID
     * @param name 物品名稱
     * @param description 物品描述
     * @param availability 物品可用性
     * @param createdAt 建立於
     * @param updatedAt 更改於
     * @return Item
     */
    public static Item reconstitute(
            ItemId id,
            long ownerId,
            ItemName name,
            ItemDescription description,
            ItemAvailability availability,
            Instant createdAt,
            Instant updatedAt) {
        return new Item(
                id,
                ownerId,
                name,
                description,
                availability,
                createdAt,
                updatedAt);
    }

    /**
     * 更新物品描述。
     *
     * @param name 物品名稱
     * @param description 物品描述
     * @param now 現在時間
     */
    public void updateDetails(
            ItemName name,
            ItemDescription description,
            Instant now) {
        ensureNotArchived();

        this.name = Objects.requireNonNull(name, "Item name must not be null");
        this.description =
                Objects.requireNonNull(description, "Item description must not be null");
        touch(now);
    }

    /**
     * 標記為可借用狀態。
     *
     * @param now 現在時間
     */
    public void makeAvailable(Instant now) {
        ensureNotArchived();
        availability = ItemAvailability.AVAILABLE;
        touch(now);
    }

    /**
     * 標記為不可借用狀態。
     *
     * @param now 現在時間
     */
    public void makeUnavailable(Instant now) {
        ensureNotArchived();
        availability = ItemAvailability.UNAVAILABLE;
        touch(now);
    }

    /**
     * 此物品已下架。
     *
     * @param now 現在時間
     */
    public void archive(Instant now) {
        availability = ItemAvailability.ARCHIVED;
        touch(now);
    }

    /**
     * 是否可以接受借閱請求。
     *
     * @return true/false
     */
    public boolean canReceiveBorrowRequest() {
        return availability.canReceiveBorrowRequest();
    }

    /**
     * 此物品是否被使用者擁有。
     *
     * @param userId 使用者 Id
     * @return true/false
     */
    public boolean isOwnedBy(long userId) {
        return ownerId == userId;
    }

    /**
     * 確保物品並不是已下架的狀態。
     */
    private void ensureNotArchived() {
        if (availability == ItemAvailability.ARCHIVED) {
            throw new ItemDomainException("Archived item cannot be changed");
        }
    }

    /**
     * 調整更新時間。
     * @param now 現在時間。
     */
    private void touch(Instant now) {
        Objects.requireNonNull(now, "Current time must not be null");

        if (now.isBefore(updatedAt)) {
            throw new ItemDomainException("Current time must not be before updated time");
        }

        updatedAt = now;
    }

    public ItemId id() {
        return id;
    }

    public long ownerId() {
        return ownerId;
    }

    public ItemName name() {
        return name;
    }

    public ItemDescription description() {
        return description;
    }

    public ItemAvailability availability() {
        return availability;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

}
