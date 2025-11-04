package Backend.Servicio.CambiarEstado;

import java.util.Arrays;

public class GeneralCambiarEstadoUtils {
    public static final String[] ESTADO_SERVICIO = {
            "activo","eliminado"
    };
    public static boolean esEstadoServicioValido(String cadena){
        return Arrays.asList(ESTADO_SERVICIO).contains(cadena.toLowerCase());
    }
}
