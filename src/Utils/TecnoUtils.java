package Utils;

import Exceptions.InvalidEmailException;
import Exceptions.InvalidGroupException;
import java.util.List;

//TODO -> podria aplicarse validaciones extremas a cada punto
public class TecnoUtils {
    private static List<String> GRUPOS_PERMITIDOS = List.of("sc","sa","cc");


    public static void validarCorreosDeUsuario(String emisor,String receptor){
        if(!emisor.contains("tecnoweb.org.bo") && !receptor.contains("tecnoweb.org.bo")){
            throw new InvalidEmailException();
        }

    }
    public static String getUserForPop3(String emailSMTP){
        return emailSMTP.substring(0,emailSMTP.indexOf("@"));
    }
    public static String generatePasswordForPop3(String userPop3) {
        String grupo = userPop3.substring(userPop3.length() - 2, userPop3.length());
        //finalizaConInicialGrupal(grupo);
        String password = userPop3.substring(0, userPop3.length() - 2);
        password += password + "*";
        password = password.replace("o","0");
        return password;
    }

//    private static void finalizaConInicialGrupal(String grupo){
//        if (!GRUPOS_PERMITIDOS.contains(grupo)){
//            throw new InvalidGroupException();
//        }
//    }
    //se asume que vendra inputs tipo LIST["*"], algo que tenga corchetes y data dentro
    public static String[] procesarString(String cadena) {
        int indexCorcheteInicial = cadena.indexOf("[");
        int indexCorcheteFinal = cadena.lastIndexOf("]");
        String data = cadena.substring(indexCorcheteInicial,indexCorcheteFinal + 1);
        String contenido = data.replaceAll("[\\[\\]]", "");
        String limpio = contenido.replace("\"", "");
        return limpio.split(",");
    }

    public static boolean tieneCorchetesYComillas(String cadena) {
        if (cadena == null) return false;

        int inicio = cadena.indexOf('[');
        int fin = cadena.lastIndexOf(']');
        if (inicio == -1 || fin == -1 || fin <= inicio) {
            return false;
        }
        String contenido = cadena.substring(inicio + 1, fin).trim();
        return contenido.matches("\\s*\"[^\"]*\"(\\s*,\\s*\"[^\"]*\")*\\s*");
    }


}
