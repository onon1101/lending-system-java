package onon1101.lendingsystem.sharedkernel.domain;

import java.util.Objects;
import org.jspecify.annotations.NonNull;

public abstract class Enumeration<K extends Comparable<K>, V>
        implements Comparable<Enumeration<K, V>> {

    private final K key;
    private final V value;

    protected Enumeration(K key, V value) {
        this.key = Objects.requireNonNull(key, "key must not be null.");
        this.value = Objects.requireNonNull(value, "value must not be null.");
    }

    public final K key() {
        return key;
    }

    public final V value() {
        return value;
    }

    ///
    /// @param other the object to be compared.
    /// @return
    @Override
    public final int compareTo(@NonNull Enumeration<K, V> other) {
        Objects.requireNonNull(other, "other must not be null.");

        if (getClass() != other.getClass()) {
            throw new IllegalArgumentException("Cannot compare different enumeration types.");
        }

        return key.compareTo(other.key);
    }

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

    @Override
    public final int hashCode() {
        return Objects.hash(getClass(), key);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
