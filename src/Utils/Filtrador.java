package Utils;

import java.util.List;
import java.util.stream.Collectors;

public class Filtrador {
    public List<String> listaDeMensajes;
    public String emisor;
    public String subject;
    public String context;

    public Filtrador(String emisor,String subject,String context,List<String> listaDeMensajes){
        this.emisor = emisor;
        this.subject = subject;
        this.context = context;
        this.listaDeMensajes = listaDeMensajes;
    }

    public List<String> filtrarMensajesDeEmisor(){
        return this.listaDeMensajes.stream().filter(message -> {
            String messageLowerCase = message.toLowerCase();
            String parametroDeBusqueda = "from: " + this.emisor.toLowerCase();
            return messageLowerCase.contains(parametroDeBusqueda);
        }).collect(Collectors.toList());
    }
    public boolean existeMensajeDelUsuario(){
        List<String> mensajesDelReceptor = this.filtrarMensajesDeEmisor();
        System.out.println("Mensaje del usuario: "+ this.emisor);
        //System.out.println(mensajesDelReceptor);
        return mensajesDelReceptor.stream().anyMatch(message -> {
           String messageLowerCase = message.toLowerCase();
            System.out.println(messageLowerCase);
           String parametroDeBusqueda = "subject: " + this.subject.toLowerCase();
            System.out.println(parametroDeBusqueda);
           return messageLowerCase.contains(parametroDeBusqueda);
        });
    }

}
