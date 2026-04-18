package fr.univ.uppa.reservation;

// Évolution du TP1 : ajout d'un message pour faciliter l'affichage CLI
public record Result<T>(T value, ErrorCode error, String message) {

    public static <T> Result<T> ok(T v, String msg) {
        return new Result<>(v, ErrorCode.NONE, msg);
    }

    public static <T> Result<T> fail(ErrorCode e, String msg) {
        return new Result<>(null, e, msg);
    }

    public boolean isOk() {
        return error == ErrorCode.NONE;
    }
}