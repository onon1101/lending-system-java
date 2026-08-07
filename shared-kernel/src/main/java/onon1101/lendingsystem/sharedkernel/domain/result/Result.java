package onon1101.lendingsystem.sharedkernel.domain.result;

import onon1101.lendingsystem.sharedkernel.IResult;

import java.util.Objects;
import java.util.function.Function;

/**
 * 成功與失敗的通用類型。
 *
 * @param <T> Payload 型別
 */
public sealed interface Result<T>
extends IResult permits Result.Success, Result.Failure {

    /**
     * 是否成功。
     *
     * @return 布林
     */
    boolean isSuccess();

    /**
     * 是否失敗。
     *
     * @return 布林
     */
    default boolean isFailure() {
        return !isSuccess();
    }

    /**
     * 匹配成功或失敗狀態，執行對應處理並返回結果。
     *
     * @param onSuccess 成功時的處理函式
     * @param onFailure 失敗時的處理函式
     * @param <R> 返回結果類型
     * @return 對應處理函式的返回值
     */
    <R> R match(
            Function<? super T, ? extends R> onSuccess,
            Function<? super String, ? extends R> onFailure);

    /**
     * 產生成功 Result。
     *
     * @param value Payload 值
     * @return 輸出 Result + T
     * @param <T> Payload 型別
     */
    static <T> Result<T> success(T value) {
        return new Success<>(value);
    }

    /**
     * 產生失敗 Result。
     *
     * @param error 業務失敗錯誤
     * @return 輸出 Result + T
     * @param <T> Payload 型別
     */
    static <T> Result<T> failure(DomainError error) {
        return new Failure<>(error);
    }
    /**
     * 表示成功結果，包含成功時返回的值。
     *
     * @param value 成功值，不能為 {@code null}
     * @param <T> 成功值類型
     */
    record Success<T>(T value) implements Result<T> {

        /**
         * 建立成功結果，並檢查成功值不可為 {@code null}。
         *
         * @throws NullPointerException 當 {@code value} 為 {@code null} 時
         */
        public Success {
            Objects.requireNonNull(value, "Success value must not be null");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isSuccess() {
            return true;
        }

        /**
         * 成功時執行 {@code onSuccess}，並將成功值傳入。
         *
         * @param onSuccess 成功時的處理函式
         * @param onFailure 失敗時的處理函式，此處不會被呼叫
         * @param <R> 返回結果類型
         * @return {@code onSuccess} 的返回值
         */
        @Override
        public <R> R match(
                Function<? super T, ? extends R> onSuccess,
                Function<? super String, ? extends R> onFailure) {
            return onSuccess.apply(value);
        }
    }

    /**
     * 表示失敗結果，包含領域錯誤資訊。
     *
     * @param error 失敗時的錯誤資訊，不能為 {@code null}
     * @param <T> 成功值類型
     */
    record Failure<T>(DomainError error) implements Result<T> {

        /**
         * 建立失敗結果，並檢查錯誤資訊不可為 {@code null}。
         *
         * @throws NullPointerException 當 {@code error} 為 {@code null} 時
         */
        public Failure {
            Objects.requireNonNull(error, "Failure error must not be null");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isSuccess() {
            return false;
        }

        /**
         * 失敗時執行 {@code onFailure}，並將錯誤代碼傳入。
         *
         * @param onSuccess 成功時的處理函式，此處不會被呼叫
         * @param onFailure 失敗時的處理函式
         * @param <R> 返回結果類型
         * @return {@code onFailure} 的返回值
         */
        @Override
        public <R> R match(
                Function<? super T, ? extends R> onSuccess,
                Function<? super String, ? extends R> onFailure) {
            return onFailure.apply(error.code());
        }
    }
}
