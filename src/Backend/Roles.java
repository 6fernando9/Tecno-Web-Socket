package Backend;

public enum Roles {
    ADMINISTRADOR("administrador"),
    CLIENTE("cliente"),
    BARBERO("barbero");

    private final String descripcion;

    Roles(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
