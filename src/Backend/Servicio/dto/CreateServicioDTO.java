package Backend.Servicio.dto;

import Backend.Utils.GeneralMethods.Resultado;
import Utils.TecnoUtils;

public class CreateServicioDTO {
    public String nombre;
    public String descripcion;
    public float precio;
    public int duracion;

    public CreateServicioDTO() {}

    public CreateServicioDTO(String nombre, String descripcion, float precio, int duracion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.duracion = duracion;
    }

    public static Resultado<CreateServicioDTO> createServicioFromSubject(String subject) {
        String[] data = TecnoUtils.procesarString(subject);

        if (data.length < 4) {
            return Resultado.error("Error... se esperaban 4 campos (nombre, descripcion, precio, duracion)");
        }

        String nombre = data[0];
        String descripcion = data[1];
        String precioStr = data[2];
        String duracionStr = data[3];

        if (nombre == null || nombre.equalsIgnoreCase("null")) {
            return Resultado.error("Error... campo nombre no puede ser nulo");
        }
        if (precioStr == null || precioStr.equalsIgnoreCase("null")) {
            return Resultado.error("Error... campo precio no puede ser nulo");
        }
        if (duracionStr == null || duracionStr.equalsIgnoreCase("null")) {
            return Resultado.error("Error... campo duracion no puede ser nulo");
        }

        float precio;
        int duracion;
        try {
            precio = Float.parseFloat(precioStr);
            duracion = Integer.parseInt(duracionStr);
        } catch (NumberFormatException e) {
            return Resultado.error("Error... datos numéricos no válidos");
        }

        if (precio <= 0) {
            return Resultado.error("Error... el precio debe ser mayor a 0");
        }
        if (duracion <= 0) {
            return Resultado.error("Error... la duración debe ser mayor a 0");
        }

        String descripcionValida = descripcion.equalsIgnoreCase("null") ? null : descripcion;

        return Resultado.ok(new CreateServicioDTO(nombre, descripcionValida, precio, duracion));
    }

    @Override
    public String toString() {
        return "CreateServicioDTO{" +
                "nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precio=" + precio +
                ", duracion=" + duracion +
                '}';
    }
}
