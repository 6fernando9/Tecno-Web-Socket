package Backend;

public enum EstadoVenta {
    PENDIENTE("pendiente"),
    CONFIRMADO("confirmado");
    private final String descripcion;
    EstadoVenta(String descripcion){
        this.descripcion = descripcion;
    }
    public String getDescripcion(){
        return this.descripcion;
    }
}
