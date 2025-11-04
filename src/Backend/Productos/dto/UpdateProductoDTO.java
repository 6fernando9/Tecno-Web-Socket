package Backend.Productos.dto;

import Backend.Utils.GeneralMethods.GeneralMethods;
import Backend.Utils.GeneralMethods.Resultado;
import Utils.TecnoUtils;

public class UpdateProductoDTO extends CreateProductoDTO{
    public Long id;
    public UpdateProductoDTO(){
        super();
    }
    public UpdateProductoDTO(Long id, String nombre,String descripcion, float precioVenta,int stockMinimo,int stockActual,String estado,String deleteAt) {
        super(nombre, descripcion,precioVenta, stockMinimo,stockActual,estado,deleteAt);
        this.id = id;
    }

    public static Resultado<UpdateProductoDTO> createUpdateProductoDTO(String subject){
        String[] data = TecnoUtils.procesarString(subject);
        if (data.length < 6) {
            return Resultado.error("Error: se esperaban al menos 6 campos (id,nombre, descripcion,precioVenta, stockMinimo,stockActual)");
        }
        String id = data[0];
        if(GeneralMethods.esCampoNuloVacio(id)){
            return Resultado.error("Error.. el campo id no puede ser nulo o vacio");
        }
        String subjectParaReutilizarCreate = String.format(
                "[\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"]",
                data[1], data[2], data[3], data[4], data[5]
        );
        Resultado<CreateProductoDTO> resultadoCreateDto = CreateProductoDTO.createProductoFromSubject(subjectParaReutilizarCreate);
        if(!resultadoCreateDto.esExitoso()){
            return Resultado.error(resultadoCreateDto.getError());
        }
        Long idDto;
        try {
            idDto = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return Resultado.error("Error: el campo 'id' debe ser numérico");
        }
        CreateProductoDTO createProductoDTO = resultadoCreateDto.getValor();
        UpdateProductoDTO updateDto = new UpdateProductoDTO(
                idDto,
                createProductoDTO.nombre,
                createProductoDTO.descripcion,
                createProductoDTO.precioVenta,
                createProductoDTO.stockMinimo,
                createProductoDTO.stockActual,
                null,
                null
        );

        return Resultado.ok(updateDto);
    }

}
