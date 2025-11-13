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
import Backend.Utils.GeneralMethods.GeneralMethods;
import Utils.SocketUtils;
import Utils.TecnoUtils;

import javax.naming.directory.AttributeInUseException;
import java.util.Scanner;
//TODO -> realizar validacion de emisor y receptor
public class TestExamen {
    public static void main(String[] args) {

        String emisor = "muerte201469@gmail.com";
        String receptor = "grupo14sc@tecnoweb.org.bo";
        String server = SocketUtils.MAIL_SERVER;
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Sistema De Peluqueria y Barberia Faceritos Via Email===");
        System.out.println("Escribe un comando o 'exit' / 'salir' / 'quit' para terminar.");

        while (true) {
            System.out.print("\n> ");
            String entrada = sc.nextLine().trim();
            if (esComandoSalida(entrada)) {
                System.out.println("👋 Saliendo del programa...");
                break;
            }
            if(entrada.equalsIgnoreCase("comandos")){
                mostrarComandos();
            }
            //String comando = "a";
            System.out.println("Ingresaste: " + entrada);
            //si tiene doble corchete y todo con comilla
            boolean tieneCorcheteYComillas = TecnoUtils.tieneCorchetesYComillas(entrada);
            if (!tieneCorcheteYComillas) {
                System.out.println("Comando no valido no tiene corchetes o no esta en el formato establecido");
                continue;
            }
            int indexCorcheteInicial = entrada.indexOf("[");
            String comando = entrada.substring(0,indexCorcheteInicial);
            // Para Usuarios
            if(comando.equalsIgnoreCase("createUser")){
                System.out.println("Ejecutando Insercion de Usuario...");
                CreateUsuario.executeCreateUsuario(emisor,receptor,server,entrada);
                continue;
            }
            if(comando.equalsIgnoreCase("updateUser")){
                System.out.println("Ejecutando Actualizacion de Usuario...");
                UpdateUsuario.executeUpdateUsuario(emisor,receptor,server,entrada);
                continue;
            }
//            if(comando.equalsIgnoreCase("deleteUser")){
//                System.out.println("Ejecutando Eliminar de Usuario...");
//                DeleteUsuario.executeDeleteUsuario(emisor,receptor,server,entrada);
//                continue;
//            }
            if(comando.equalsIgnoreCase("listarUsuarios")){
                System.out.println("Ejecutando Listar Usuario...");
                ListarUsuario.executeListarUsuario(emisor,receptor,server,entrada);
                continue;
            }
            //Para Productos
            if(comando.equalsIgnoreCase("createProducto")){
                System.out.println("Ejecutando Crear Producto");
                CreateProducto.executeCreateProducto(emisor,receptor,server,entrada);
                continue;
            }
            if(comando.equalsIgnoreCase("updateProducto")){
                System.out.println("Ejecutando Update Producto");
                UpdateProducto.executeUpdateProducto(emisor,receptor,server,entrada);
                continue;
            }
            if(comando.equalsIgnoreCase("listarProductoSimple")){
                System.out.println("Ejecutando Listar Producto Simple");
                ListarStockActualSimple.executeListarStockActualSimple(emisor,receptor,server,entrada);
                continue;
            }
            if(comando.equalsIgnoreCase("listarProductoIntervalo")){
                System.out.println("Ejecutando Listar Producto Intervalo");
                ListarStockActualIntervalo.executeListarStockActualIntervalo(emisor,receptor,server,entrada);
                continue;
            }
            //Para Servicios
            if(comando.equalsIgnoreCase("createServicio")){
                System.out.println("Ejecutando Crear Servicio");
                CreateServicio.executeCreateServicio(emisor,receptor,server,entrada);
                continue;
            }
            if(comando.equalsIgnoreCase("updateServicio")){
                System.out.println("Ejecutando Update Servicio");
                UpdateServicio.executeUpdateServicio(emisor,receptor,server,entrada);
                continue;
            }
            if(comando.equalsIgnoreCase("listarServicioSimple")){
                System.out.println("Ejecutando Listar Servicio Simple");
                ListarServicioPrecioSimple.executeListarServicioPrecioSimple(emisor,receptor,server,entrada);
                continue;
            }
            if(comando.equalsIgnoreCase("listarServicioIntervalo")){
                System.out.println("Ejecutando Listar Servicio Intervalo");
                ListarServicioPrecioIntervalo.executeListarServiciosPrecioIntervalo(emisor,receptor,server,entrada);
                continue;
            }
            //Para Pagos
            if(comando.equalsIgnoreCase("createPago")){
                System.out.println("Ejecutando Crear Pago");
                CreatePago.executeCrearPago(emisor,receptor,server,entrada);
                continue;
            }
            if(comando.equalsIgnoreCase("deletePago")){
                System.out.println("Ejecutando Delete Pago");
                DeletePago.executeDeletePago(emisor,receptor,server,entrada);
                continue;
            }
            if(comando.equalsIgnoreCase("listarPagosDeVenta")){
                System.out.println("Ejecutando Listar pago de venta");
                ListarPagoDeVenta.executeListarPagoDeVenta(emisor,receptor,server,entrada);
                continue;
            }
            //Para Horarios
            if(comando.equalsIgnoreCase("createHorario")){
                System.out.println("Ejecutando Crear Horario");
                CreateHorario.executeCrearHorario(emisor,receptor,server,entrada);
                continue;
            }
            if(comando.equalsIgnoreCase("updateHorario")){
                System.out.println("Ejecutando Delete Pago");
                UpdateHorario.executeUpdateHorario(emisor,receptor,server,entrada);
                continue;
            }
            if(comando.equalsIgnoreCase("deleteHorario")){
                System.out.println("Ejecutando Listar pago de venta");
                DeleteHorario.executeDeleteHorario(emisor,receptor,server,entrada);
                continue;
            }
            if(comando.equalsIgnoreCase("listarHorarioDeBarbero")){
                System.out.println("Ejecutando Listar pago de venta");
                ListarHorarioDeBarbero.executeListarHorarioDeBarbero(emisor,receptor,server,entrada);
                continue;
            }
            /// demas metodos

            //en el caso de que no sea ninguna
            System.out.println("Comando no encontrado");
        }
        sc.close();
    }

    public static boolean esComandoSalida(String entrada){
        return entrada.equalsIgnoreCase("exit") ||
                entrada.equalsIgnoreCase("salir") ||
                entrada.equalsIgnoreCase("quit");
    }
    public static void mostrarComandos() {
        System.out.println("\n=== 📋 LISTA DE COMANDOS DISPONIBLES ===\n");

        // ---------- USUARIOS ----------
        System.out.println("👤 USUARIOS");
        System.out.println("createUser[\"nombre\",\"apellido\",\"email\",\"telefono\",\"password\",\"rol\"]");
        System.out.println("   → Crea un nuevo usuario en el sistema.");
        System.out.println("updateUser[\"id\",\"nombre\",\"apellido\",\"email\",\"telefono\",\"password\",\"rol\"]");
        System.out.println("   → Actualiza los datos de un usuario existente.");
        System.out.println("listarUsuarios[\"rol\"]  o  listarUsuarios[\"*\"]");
        System.out.println("   → Lista usuarios por rol o todos si se usa *.");
        System.out.println();

        // ---------- PRODUCTOS ----------
        System.out.println("📦 PRODUCTOS");
        System.out.println("createProducto[\"nombre\",\"descripcion\",\"precioVenta\",\"stockMinimo\",\"stockActual\"]");
        System.out.println("   → Inserta un nuevo producto.");
        System.out.println("updateProducto[\"id\",\"nombre\",\"descripcion\",\"precioVenta\",\"stockMinimo\",\"stockActual\"]");
        System.out.println("   → Actualiza un producto existente.");
        System.out.println("listarProductoSimple[\"signovalor\"]  Ej: listarProductoSimple[>10]");
        System.out.println("   → Lista productos cuyo stock cumple la condición.");
        System.out.println("listarProductoIntervalo[\"min\",\"max\"]");
        System.out.println("   → Lista productos cuyo stock está dentro del intervalo.");
        System.out.println();

        // ---------- SERVICIOS ----------
        System.out.println("💈 SERVICIOS");
        System.out.println("createServicio[\"nombre\",\"descripcion\",\"precio\",\"duracion\"]");
        System.out.println("   → Crea un nuevo servicio (peluquería/barbería).");
        System.out.println("updateServicio[\"id\",\"nombre\",\"descripcion\",\"precio\",\"duracion\"]");
        System.out.println("   → Actualiza los datos de un servicio existente.");
        System.out.println("listarServicioSimple[\"signovalor\"]  Ej: listarServicioSimple[>50]");
        System.out.println("   → Lista servicios cuyo precio cumple la condición.");
        System.out.println("listarServicioIntervalo[\"min\",\"max\"]");
        System.out.println("   → Lista servicios dentro de un rango de precios.");
        System.out.println();

        // ---------- PAGOS ----------
        System.out.println("💰 PAGOS");
        System.out.println("createPago[\"idVenta\",\"monto\",\"metodoPago\"]");
        System.out.println("   → Registra un pago asociado a una venta.");
        System.out.println("deletePago[\"idPago\"]");
        System.out.println("   → Elimina un pago por su ID.");
        System.out.println("listarPagosDeVenta[\"idVenta\"]");
        System.out.println("   → Lista todos los pagos asociados a una venta.");
        System.out.println();

        // ---------- HORARIOS ----------
        System.out.println("🕒 HORARIOS");
        System.out.println("createHorario[\"idBarbero\",\"dia\",\"horaInicio\",\"horaFin\"]");
        System.out.println("   → Crea un nuevo horario para un barbero.");
        System.out.println("updateHorario[\"idBarbero\",\"idHorario\",\"horaInicio\",\"horaFin\"]");
        System.out.println("   → Modifica un horario existente.");
        System.out.println("deleteHorario[\"idBarbero\",\"idHorario\"]");
        System.out.println("   → Elimina un horario registrado.");
        System.out.println("listarHorarioDeBarbero[\"idBarbero\"]");
        System.out.println("   → Lista los horarios de un barbero específico.");
        System.out.println();

        // ---------- COMANDOS GENERALES ----------
        System.out.println("⚙️ GENERALES");
        System.out.println("comandos");
        System.out.println("   → Muestra esta lista de comandos.");
        System.out.println("exit / salir / quit");
        System.out.println("   → Finaliza la ejecución del programa.");
        System.out.println("\n=========================================\n");
    }

}
