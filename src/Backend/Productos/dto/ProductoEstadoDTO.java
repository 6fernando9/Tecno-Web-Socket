package Backend.Productos.dto;

import Backend.Productos.CambiarEstado.GeneralCambiarEstadoUtils;
import Backend.Usuarios.dto.UsuarioEstadoDTO;
import Backend.Utils.GeneralMethods.GeneralMethods;
import Backend.Utils.GeneralMethods.Resultado;
import Utils.TecnoUtils;

public class ProductoEstadoDTO {
    public Long id;
    public String estado;
    public ProductoEstadoDTO(){}
    public ProductoEstadoDTO(Long id,String estado){
        this.id = id;
        this.estado = estado;
    }
    public static Resultado<ProductoEstadoDTO> crearProductoEstadoDto(String subject){
        String[] data = TecnoUtils.procesarString(subject);
        if(data.length < 2){
            return  Resultado.error("Error..se esperaba al menos 2 campos(id,estado)");
        }
        String id = data[0];
        String estado = data[1];
        if(GeneralMethods.esCampoNuloVacio(id)){
            return Resultado.error("Error..el campo id no puede ser nulo o vacio");
        }
        if(GeneralMethods.esCampoNuloVacio(estado)){
            return Resultado.error("Error..el campo estado no puede ser nulo o vacio");
        }
        if(!GeneralCambiarEstadoUtils.esEstadoProductoValido(estado)){
            return Resultado.error("Error..el valor del estado no es valido");
        }
        Long idDto;
        try{
            idDto = Long.parseLong(id);
        }catch (NumberFormatException e){
            return Resultado.error("Error..el campo id debe ser numerico");
        }
        return Resultado.ok(new ProductoEstadoDTO(idDto,estado));
    }
}
