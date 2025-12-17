package Backend.Movimientos.dto;

import Backend.Productos.dto.UpdateProductoDTO;

public class MovimientoDTO {
    public Long id;
    public String tipoMovimiento;
    public int cantidad;
    public String motivo;
    public String fecha;
    public String estado;
    public UpdateProductoDTO producto;
    public MovimientoDTO(){}
    public MovimientoDTO(Long id, String tipoMovimiento,int cantidad, String motivo,String fecha,String estado,UpdateProductoDTO producto){
        this.id = id;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.motivo = motivo;
        this.fecha = fecha;
        this.producto = producto;
        this.estado = estado;

    }
}
