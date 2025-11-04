package Backend.Usuarios.dto;

import Backend.Usuarios.CambiarEstado.GeneralCambiarEstadoUtils;
import Backend.Utils.GeneralMethods.GeneralMethods;
import Backend.Utils.GeneralMethods.Resultado;
import Utils.TecnoUtils;

public class UsuarioEstadoDTO {
    Long id;
    String estado;
    public UsuarioEstadoDTO(){}
    public UsuarioEstadoDTO(Long id,String estado){
        this.id = id;
        this.estado = estado;
    }
    public static Resultado<UsuarioEstadoDTO> crearUsuarioEstadoFromSubject(String subject){
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
        if(!GeneralCambiarEstadoUtils.esEstadoUsuarioValido(estado)){
            return Resultado.error("Error..el valor del estado no es valido");
        }
        Long idDto;
        try{
            idDto = Long.parseLong(id);
        }catch (NumberFormatException e){
            return Resultado.error("Error..el campo id debe ser numerico");
        }
        return Resultado.ok(new UsuarioEstadoDTO(idDto,estado));
    }
    public static void main(String[] args){
        String subject = """
                crear["s","activo"]
                """;
        subject = GeneralMethods.parsearSubjectComillaTriple(subject);
        Resultado<UsuarioEstadoDTO> res = UsuarioEstadoDTO.crearUsuarioEstadoFromSubject(subject);
        if(!res.esExitoso()){
            System.out.println(res.getError());
        }else{
            System.out.println("exito");
        }

    }
}
