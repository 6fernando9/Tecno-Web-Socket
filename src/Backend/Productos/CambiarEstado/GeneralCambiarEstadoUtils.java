package Backend.Productos.CambiarEstado;

import java.util.Arrays;

public class GeneralCambiarEstadoUtils {
    public static final String[] ESTADO_PRODUCTO = {
            "activo","eliminado"
    };
    public static boolean esEstadoProductoValido(String cadena){
        return Arrays.asList(ESTADO_PRODUCTO).contains(cadena.toLowerCase());
    }
}
