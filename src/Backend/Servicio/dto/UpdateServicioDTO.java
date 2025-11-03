package Backend.Servicio.dto;

import Backend.Usuarios.dto.UpdateUsuarioDTO;
import Backend.Utils.GeneralMethods.Resultado;
import Exceptions.InvalidDataException;
import Utils.TecnoUtils;

public class UpdateServicioDTO extends CreateServicioDTO{
    public Long id;
    public UpdateServicioDTO(){
        super();
    }
    public UpdateServicioDTO(Long id,String nombre, String descripcion, float precio, int duracion) {
        super(nombre,descripcion,precio,duracion);
        this.id = id;
    }

    public static Resultado<UpdateServicioDTO> crearUpdateServicioMedianteSubject(String subject) throws InvalidDataException {
        String[] data = TecnoUtils.procesarString(subject);
        if (data.length < 5) {
            return Resultado.error("Error: se esperaban al menos 5 campos (id, nombre, descripcion,precio,duracion)");
        }
        String id = data[0];
        String subjectParaReutilizarCreate = String.format(
                "[\"%s\",\"%s\",\"%s\",\"%s\"]",
                data[1], data[2], data[3], data[4]
        );
        Resultado<CreateServicioDTO> resultadoCreateDTO = CreateServicioDTO.createServicioFromSubject(subjectParaReutilizarCreate);
        if(!resultadoCreateDTO.esExitoso()){
            return Resultado.error(resultadoCreateDTO.getError());
        }
        if(id == null || id.equalsIgnoreCase("null")){
            return Resultado.error("Error..el campo id no puede ser nulo");
        }
        CreateServicioDTO createServicioDTO = resultadoCreateDTO.getValor();
        Long idDto;
        try {
            idDto = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return Resultado.error("Error: el campo 'id' debe ser numérico");
        }

        return Resultado.ok(new UpdateServicioDTO(
                idDto,
                createServicioDTO.nombre,
                createServicioDTO.descripcion,
                createServicioDTO.precio,
                createServicioDTO.duracion
        ));
    }
}
