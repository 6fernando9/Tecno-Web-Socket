package Backend.Pagos.dto;

public class PagoDTO {
    public Long id;
    public float monto;
    public String tipoPago;
    public PagoDTO(){}
    public PagoDTO(Long id,float monto, String tipoPago){
        this.id = id;
        this.monto = monto;
        this.tipoPago = tipoPago;
    }
    @Override
    public String toString() {
        return "PagoDTO{" +
                "id=" + id +
                ", monto=" + monto +
                ", tipoPago='" + tipoPago + '\'' +
                '}';
    }
}
