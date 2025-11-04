package Backend.Usuarios.CambiarEstado;

import java.lang.reflect.Array;
import java.util.Arrays;

public class GeneralCambiarEstadoUtils {
    public static final String[] ESTADO_USUARIO = {
            "activo","eliminado"
    };
    public static boolean esEstadoUsuarioValido(String cadena){
        return Arrays.asList(ESTADO_USUARIO).contains(cadena.toLowerCase());
    }
}
