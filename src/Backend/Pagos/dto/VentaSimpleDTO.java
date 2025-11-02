package Backend.Pagos.dto;

import java.util.ArrayList;
import java.util.List;

public class VentaSimpleDTO {
    public Long id;
    public float montoTotal;
    public String estado;
    public String tipoPago;
    public String fecha;
    List<PagoDTO> listaPagos;
    public VentaSimpleDTO() {}

    public VentaSimpleDTO(Long id, float montoTotal, String estado, String tipoPago, String fecha) {
        this.id = id;
        this.montoTotal = montoTotal;
        this.estado = estado;
        this.tipoPago = tipoPago;
        this.fecha = fecha;
        this.listaPagos = new ArrayList<>();
    }

    public List<PagoDTO> getListaPagos() {
        return listaPagos;
    }

    public void setListaPagos(List<PagoDTO> listaPagos) {
        this.listaPagos = listaPagos;
    }

    @Override
    public String toString() {
        return "VentaSimpleDTO{" +
                "id=" + id +
                ", montoTotal=" + montoTotal +
                ", estado='" + estado + '\'' +
                ", tipoPago='" + tipoPago + '\'' +
                ", fecha='" + fecha + '\'' +
                '}';
    }
}
