package Backend;

public enum TipoMovimiento {
    ENTRADA("entrada"),
    SALIDA("salida"),
    AJUSTE("ajuste");

    private final String descripcion;

    TipoMovimiento(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

}
