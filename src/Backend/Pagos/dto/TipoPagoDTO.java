package Backend.Pagos.dto;

public class TipoPagoDTO {
    public Long id;
    public String nombre;
    public TipoPagoDTO(){}
    public TipoPagoDTO(Long id,String nombre){
        this.id = id;
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "TipoPagoDTO{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
