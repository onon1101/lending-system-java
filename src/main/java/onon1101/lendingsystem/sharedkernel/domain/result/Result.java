package onon1101.lendingsystem.sharedkernel.domain.result;

import java.util.Objects;
import java.util.function.Function;

public sealed interface Result<T> permits Result.Success, Result.Failure {

    boolean isSuccess();

    default boolean isFailure() {
        return !isSuccess();
    }

    <R> R match(
            Function<? super T, ? extends R> onSuccess,
            Function<? super DomainError, ? extends R> onFailure);

    static <T> Result<T> success(T value) {
        return new Success<>(value);
    }

    static <T> Result<T> failure(DomainError error) {
        return new Failure<>(error);
    }

    record Success<T>(T value) implements Result<T> {

        public Success {
            Objects.requireNonNull(value, "Success value must not be null");
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public <R> R match(
                Function<? super T, ? extends R> onSuccess,
                Function<? super DomainError, ? extends R> onFailure) {
            return onSuccess.apply(value);
        }
    }

    record Failure<T>(DomainError error) implements Result<T> {

        public Failure {
            Objects.requireNonNull(error, "Failure error must not be null");
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public <R> R match(
                Function<? super T, ? extends R> onSuccess,
                Function<? super DomainError, ? extends R> onFailure) {
            return onFailure.apply(error);
        }
    }
}
