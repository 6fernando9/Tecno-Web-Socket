package Backend.Utils.GeneralMethods;

public class GeneralMethods {
    public static String parsearSubjectComillaTriple(String subject){
        subject = subject.trim();
        return subject.replace("\r", "").replace("\n", " ");
    }
}
