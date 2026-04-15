package fr.univ.uppa.reservation;

/**
 * Minimal "API-like" result for TP1.
 * Students assert on error codes in tests (Given/When/Then).
 */
public record Result<T>(T value, ErrorCode error) {

    public static <T> Result<T> ok(T v) {
        return new Result<>(v, ErrorCode.NONE);
    }

    public static <T> Result<T> fail(ErrorCode e) {
        return new Result<>(null, e);
    }

    public boolean isOk() {
        return error == ErrorCode.NONE;
    }
}