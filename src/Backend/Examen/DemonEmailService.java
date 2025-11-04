package Backend.Examen;

import Backend.Horarios.CrearHorario.CreateHorario;
import Backend.Horarios.DeleteHorario.DeleteHorario;
import Backend.Horarios.ListarHorario.ListarHorarioDeBarbero;
import Backend.Horarios.UpdateHorario.UpdateHorario;
import Backend.Pagos.CreatePago.CreatePago;
import Backend.Pagos.DeletePago.DeletePago;
import Backend.Pagos.ListPagos.ListarPagoDeVenta;
import Backend.Productos.CreateProducto.CreateProducto;
import Backend.Productos.ListarProducto.ListarStockActualIntervalo;
import Backend.Productos.ListarProducto.ListarStockActualSimple;
import Backend.Productos.UpdateProducto.UpdateProducto;
import Backend.Servicio.CreateServicio.CreateServicio;
import Backend.Servicio.ListarServicio.ListarServicioPrecioIntervalo;
import Backend.Servicio.ListarServicio.ListarServicioPrecioSimple;
import Backend.Servicio.UpdateServicio.UpdateServicio;
import Backend.Usuarios.CreateUser.CreateUsuario;
import Backend.Usuarios.ListarUser.ListarUsuario;
import Backend.Usuarios.UpdateUser.UpdateUsuario;
import POP3.Pop3Service;
import SMTP.SMTPClient;
import Utils.SocketUtils;
import Utils.TecnoUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class DemonEmailService {
    Set<String> idEmailsProcesados;
    Pop3Service pop3Service;

    public DemonEmailService() {
        this.pop3Service = new Pop3Service();
        this.idEmailsProcesados = new HashSet<>();
    }
    public void executeDemonEmailService(){
        System.out.println("=== SERVICIO DE EMAIL INICIADO ===");
        System.out.println("Cuenta: grupo14sc@tecnoweb.org.bo");
        System.out.println("Servidor BD: mail.tecnoweb.org.bo");
        System.out.println("Base de datos: db_grupo14sc");
        System.out.println("================================\n");
        String grupoReceptor = "grupo14sc@tecnoweb.org.bo";
        try {
            int ciclo = 1;
            while (true) {
                try {
                    System.out.println("--- Ciclo #" + ciclo + " ---");
                    System.out.println("[POP3] Conectando...");
                    pop3Service.connect();//conecta con credenciales validas
                    System.out.println("[POP3] ✓ Conectado");
                    // Obtener cantidad de correos
                    List<String> idsPop3 = this.pop3Service.obtenerListaDeIds();
                    int dimensionIdsPop3 = idsPop3.size();
                    System.out.println("[POP3] Correos en el buzón: " + idsPop3.size());
                    if (dimensionIdsPop3 > 0) {
                        for (String id: idsPop3) {
                            int idProcesado = Integer.parseInt(id);
                            if (!this.idEmailsProcesados.contains(id)) {
                                String emisor = this.pop3Service.getFrom(idProcesado);
                                String subject = this.pop3Service.getSubject(idProcesado);
                                System.out.println("emisor: " + emisor);
                                System.out.println("receptor: " + grupoReceptor);
                                System.out.println("subject: " + subject);
                                ejecutarComandos(emisor,grupoReceptor,SocketUtils.MAIL_SERVER,subject);
                                this.idEmailsProcesados.add(id);
                            }else{
                                System.out.println("id " + id + "ya existe");
                            }
                        }
                    } else {
                        System.out.println("[INFO] No hay correos nuevos");
                    }

                    this.pop3Service.close();
                    System.out.println("[POP3] ✓ Desconectado\n");

                    // Esperar 30 segundos antes del siguiente ciclo
                    System.out.println("Esperando 15 segundos...\n");
                    Thread.sleep(15000);
                    ciclo++;

                } catch (IOException e) {
                    System.err.println("[ERROR POP3] " + e.getMessage());
                    try {
                        this.pop3Service.close();
                    } catch (Exception ex) {}
                    Thread.sleep(10000); // Esperar 10 segundos si hay error
                }
            }

        } catch (InterruptedException e) {
            System.out.println("\n[INFO] Servicio detenido");
        }
    }
    public static void ejecutarComandos(String emisor,String receptor,String server,String subject){

        //si tiene doble corchete y todo con comilla
        SMTPClient smtpClient = new SMTPClient(server,receptor,emisor);
        //podriamos hacer un comando previo para que busque en los comandos solamente si no lo encuentra retornar que no hay ese comando
        boolean tieneCorcheteYComillas = TecnoUtils.tieneCorchetesYComillas(subject);
        if (!tieneCorcheteYComillas) {
            smtpClient.sendDataToServer("ERROR.. COMANDO NO VALIDO","Comando no valido no tiene corchetes o no esta en el formato establecido\r\n");
            return;
        }
        int indexCorcheteInicial = subject.indexOf("[");
        String comando = subject.substring(0,indexCorcheteInicial);
        System.out.println("comando: "+ comando);
        // Para Usuarios
        if(comando.equalsIgnoreCase("createUser")){
            System.out.println("Ejecutando Insercion de Usuario...");
            CreateUsuario.executeCreateUsuarioDemon(emisor,receptor,server,subject);
            return;
        }
        if(comando.equalsIgnoreCase("updateUser")){
            System.out.println("Ejecutando Actualizacion de Usuario...");
            UpdateUsuario.executeUpdateUsuarioDemon(emisor,receptor,server,subject);
            return;
        }
//            if(comando.equalsIgnoreCase("deleteUser")){
//                System.out.println("Ejecutando Eliminar de Usuario...");
//                DeleteUsuario.executeDeleteUsuario(emisor,receptor,server,subject);
//                return;
//            }
        if(comando.equalsIgnoreCase("listarUsuarios")){
            System.out.println("Ejecutando Listar Usuario...");
            ListarUsuario.executeListarUsuarioDemon(emisor,receptor,server,subject);
            return;
        }
        //Para Productos
        if(comando.equalsIgnoreCase("createProducto")){
            System.out.println("Ejecutando Crear Producto");
            CreateProducto.executeCreateProductoDemon(emisor,receptor,server,subject);
            return;
        }
        if(comando.equalsIgnoreCase("updateProducto")){
            System.out.println("Ejecutando Update Producto");
            UpdateProducto.executeUpdateProductoDemon(emisor,receptor,server,subject);
            return;
        }
        //listarProductoSimple[">5"]
        if(comando.equalsIgnoreCase("listarProductoSimple")){
            System.out.println("Ejecutando Listar Producto Simple");
            ListarStockActualSimple.executeListarStockActualSimpleDemon(emisor,receptor,server,subject);
            return;
        }
        if(comando.equalsIgnoreCase("listarProductoIntervalo")){
            System.out.println("Ejecutando Listar Producto Intervalo");
            ListarStockActualIntervalo.executeListarStockActualIntervaloDemon(emisor,receptor,server,subject);
            return;
        }
        //Para Servicios
        if(comando.equalsIgnoreCase("createServicio")){
            System.out.println("Ejecutando Crear Servicio");
            CreateServicio.executeCreateServicioDemon(emisor,receptor,server,subject);
            return;
        }
        if(comando.equalsIgnoreCase("updateServicio")){
            System.out.println("Ejecutando Update Servicio");
            UpdateServicio.executeUpdateServicioDemon(emisor,receptor,server,subject);
            return;
        }
        if(comando.equalsIgnoreCase("listarServicioSimple")){
            System.out.println("Ejecutando Listar Servicio Simple");
            ListarServicioPrecioSimple.executeListarServicioPrecioSimpleDemon(emisor,receptor,server,subject);
            return;
        }
        if(comando.equalsIgnoreCase("listarServicioIntervalo")){
            System.out.println("Ejecutando Listar Servicio Intervalo");
            ListarServicioPrecioIntervalo.executeListarServiciosPrecioIntervaloDemon(emisor,receptor,server,subject);
            return;
        }
        //Para Pagos
        if(comando.equalsIgnoreCase("createPago")){
            System.out.println("Ejecutando Crear Pago");
            CreatePago.executeCrearPagoDemon(emisor,receptor,server,subject);
            return;
        }
        if(comando.equalsIgnoreCase("deletePago")){
            System.out.println("Ejecutando Delete Pago");
            DeletePago.executeDeletePagoDemon(emisor,receptor,server,subject);
            return;
        }
        if(comando.equalsIgnoreCase("listarPagosDeVenta")){
            System.out.println("Ejecutando Listar pago de venta");
            ListarPagoDeVenta.executeListarPagoDeVentaDemon(emisor,receptor,server,subject);
            return;
        }
        //Para Horarios
        if(comando.equalsIgnoreCase("createHorario")){
            System.out.println("Ejecutando Crear Horario");
            CreateHorario.executeCrearHorarioDemon(emisor,receptor,server,subject);
            return;
        }
        if(comando.equalsIgnoreCase("updateHorario")){
            System.out.println("Ejecutando Delete Pago");
            UpdateHorario.executeUpdateHorarioDemon(emisor,receptor,server,subject);
            return;
        }
        if(comando.equalsIgnoreCase("deleteHorario")){
            System.out.println("Ejecutando Listar pago de venta");
            DeleteHorario.executeDeleteHorarioDemon(emisor,receptor,server,subject);
            return;
        }
        if(comando.equalsIgnoreCase("listarHorarioDeBarbero")){
            System.out.println("Ejecutando Listar pago de venta");
            ListarHorarioDeBarbero.executeListarHorarioDeBarberoDemon(emisor,receptor,server,subject);
            return;
        }
        /// demas metodos
        smtpClient.sendDataToServer("ERROR.. COMANDO NO ENCONTRADO","Comando no encontrado\r\n");
        return;

    }
    public static void main(String[] args){
        DemonEmailService demonEmailService = new DemonEmailService();
        demonEmailService.executeDemonEmailService();
    }
}
