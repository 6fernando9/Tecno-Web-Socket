package Backend.Utils.GeneralMethods;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneralMethods {
    public static String parsearSubjectComillaTriple(String subject){
        subject = subject.trim();
        return subject.replace("\r", "").replace("\n", " ");
    }
    public static boolean tieneComillaLosCorchetes(String cadena){
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(cadena);
        if (!matcher.find()) {
            return false;
        }
        String contenido = matcher.group(1);
        boolean valido = contenido.matches("\\s*\"[^\"]*\"(\\s*,\\s*\"[^\"]*\")*\\s*");
        return valido;
    }
    public static boolean esCampoNuloVacio(String cadena){
        return cadena == null || cadena.trim().isEmpty() || cadena.equalsIgnoreCase("null");
    }
}
