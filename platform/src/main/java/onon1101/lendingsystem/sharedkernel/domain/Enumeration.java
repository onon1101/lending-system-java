package onon1101.lendingsystem.sharedkernel.domain;

import java.util.Objects;

/**
 * Enumeration class 的基類。
 *
 * @param <K> Key 的型別。
 * @param <V> Value 的型別。
 */
public abstract class Enumeration<K extends Comparable<K>, V>
        implements Comparable<Enumeration<K, V>> {

    /** Key */
    private final K key;

    /** Value */
    private final V value;

    /**
     * Constructor.
     *
     * @param key 鍵
     * @param value 值
     */
    protected Enumeration(K key, V value) {
        this.key = Objects.requireNonNull(key, "key must not be null.");
        this.value = Objects.requireNonNull(value, "value must not be null.");
    }

    /**
     * Key's getter。
     *
     * @return Key
     */
    public final K key() {
        return key;
    }

    /**
     * Value's getter.
     *
     * @return Value's getter
     */
    public final V value() {
        return value;
    }

    ///
    /// @param other the object to be compared.
    ///
    /// @return The number that the same as object.
    @Override
    public final int compareTo(Enumeration<K, V> other) {
        Objects.requireNonNull(other, "other must not be null.");

        if (getClass() != other.getClass()) {
            throw new IllegalArgumentException("Cannot compare different enumeration types.");
        }

        return key.compareTo(other.key);
    }

    /// Compare the object which whether is the same or not.
    ///
    /// @param object   the reference object with which to compare.
    /// @return boolean
    @Override
    public final boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Enumeration<?, ?> other = (Enumeration<?, ?>) object;
        return key.equals(other.key);
    }

    /// hash code of key.
    ///
    /// @return string
    @Override
    public final int hashCode() {
        return Objects.hash(getClass(), key);
    }

    /// get value
    ///
    /// @return value's string
    @Override
    public String toString() {
        return value.toString();
    }
}
