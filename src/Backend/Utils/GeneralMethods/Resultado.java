package Backend.Utils.GeneralMethods;

//patron para errores
public class Resultado<T> {
    private final String error;
    private final T valor;

    private Resultado(String error, T valor) {
        this.error = error;
        this.valor = valor;
    }

    public static <T> Resultado<T> ok(T valor) {
        return new Resultado<>(null, valor);
    }

    public static <T> Resultado<T> error(String mensaje) {
        return new Resultado<>(mensaje, null);
    }

    public boolean esExitoso() {
        return error == null;
    }

    public String getError() {
        return error;
    }

    public T getValor() {
        return valor;
    }
}
