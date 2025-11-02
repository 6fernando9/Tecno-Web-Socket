package Backend.Productos.dto;

import Backend.Usuarios.Resultado;
import Utils.TecnoUtils;

public class CreateProductoDTO {
    public String nombre;
    public String descripcion;
    //podria ser Big Decimal
    public float precioVenta;
    public int stockMinimo;
    public int stockActual;

    public CreateProductoDTO(){}
    public CreateProductoDTO(String nombre,String descripcion, float precioVenta,int stockMinimo,int stockActual){
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioVenta = precioVenta;
        this.stockMinimo = stockMinimo;
        this.stockActual = stockActual;
    }
    public static Resultado<CreateProductoDTO> createProductoFromSubject(String subject){
        String[] data = TecnoUtils.procesarString(subject);
        if (data.length < 5) {
            return Resultado.error("Error... se esperaba al menos 5 campos (nombre,descripcion,precioVenta,stockMinimo,stockMaximo)");
        }
        String nombre = data[0];
        String descripcion = data[1];
        String precioVenta = data[2];
        String stockMinimo = data[3];
        String stockActual = data[4];
        if(precioVenta == null || precioVenta.equalsIgnoreCase("null")){
            return Resultado.error("Error.. campo precio venta no puede ser nulo");
        }
        if(stockMinimo == null || stockMinimo.equalsIgnoreCase("null")){
            return Resultado.error("Error.. campo stock Minimo venta no puede ser nulo");
        }
        if(stockActual == null || stockActual.equalsIgnoreCase("null")){
            return Resultado.error("Error.. campo stock Maximo venta no puede ser nulo");
        }
        float precioVentaDto;
        int stockMinimoDto;
        int stockMaximoDto;
        try{
            precioVentaDto = Float.parseFloat(precioVenta);
            stockMinimoDto = Integer.parseInt(stockMinimo);
            stockMaximoDto = Integer.parseInt(stockActual);
        }catch(NumberFormatException e){
            return Resultado.error("Error.. datos numericos no validos");
        }

        if(nombre == null || nombre.equalsIgnoreCase("null")){
            return Resultado.error("Error.. campo nombre no puede ser nulo");
        }
        //validacion nula a proposito esquivada
//        if(descripcion == null || nombre.equalsIgnoreCase("null")){
//            return Resultado.error("Error.. campo descripcion no puede ser nulo");
//        }
        if(precioVentaDto <= 0){
            return Resultado.error("Error.. campo precio venta no puede ser menor a 0");
        }
        if(stockMinimoDto <= 0){
            return Resultado.error("Error.. campo stock Minimo no puede ser menor a 0");
        }
        if(stockMaximoDto <= 0){
            return Resultado.error("Error.. campo stock Maximo no puede ser menor a 0");
        }
        String descripcionDto = descripcion.equalsIgnoreCase("null") ? null : descripcion;
        return Resultado.ok(new CreateProductoDTO(nombre,descripcionDto,precioVentaDto,stockMinimoDto,stockMaximoDto));

    }
}
