package Backend.Utils.dto;

import Backend.Utils.GeneralMethods.Resultado;
import Utils.TecnoUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComparadorSigno {
    public String signo;
    public Double valor;
    public ComparadorSigno(){}
    public ComparadorSigno(String signo,Double valor){
        this.signo = signo;
        this.valor = valor;
    }
    public static Resultado<ComparadorSigno> crearComparadorFromSubject(String subject){
        String[] data = TecnoUtils.procesarString(subject);
        if(data.length < 1){
            return Resultado.error("Error..se esperaba al menos un valor");
        }
        String valorSigno = data[0];
        String signoRegex = "^(>=|<=|>|<|==|=|!=)";
        Pattern p = Pattern.compile(signoRegex);
        Matcher m = p.matcher(valorSigno);
        //verificar si es =,<,>,>=,<=
        if(!m.find()){
            return Resultado.error("Error...no se encontro ningun signo");
        }
        String signo = m.group(1);
        String valorStr = valorSigno.substring(m.end()).trim();
        if (!valorStr.matches("^-?\\d+(\\.\\d+)?$"))
            return Resultado.error("Error..no se identifico ningun valor numerico");
        Double valor = Double.parseDouble(valorStr);
        return Resultado.ok(new ComparadorSigno(signo,valor));
    }
    //buena idea si queriamos hacer bien dinamico el between
    public static Resultado<ComparadorSigno[]> crearComparadorIntervaloFromSubject(String subject){
        String[] data = TecnoUtils.procesarString(subject);
        if(data.length < 2){
            return Resultado.error("Error..se esperaba al menos un valor");
        }
        String subjectParaReutilizarCreate1 = String.format(
                "[\"%s\"]",
                data[0]
        );
        String subjectParaReutilizarCreate2 = String.format(
                "[\"%s\"]",
                data[1]
        );
        Resultado<ComparadorSigno> resultadoComparador1 = ComparadorSigno.crearComparadorFromSubject(subjectParaReutilizarCreate1);
        Resultado<ComparadorSigno> resultadoComparador2 = ComparadorSigno.crearComparadorFromSubject(subjectParaReutilizarCreate2);
        if (!resultadoComparador1.esExitoso() || !resultadoComparador2.esExitoso()) {
            return Resultado.error("Los compos no cumplen con las condiciones establecidas");
        }
         //si ambos son exitosos
        ComparadorSigno comparadorSigno1 = resultadoComparador1.getValor();
        ComparadorSigno comparadorSigno2 = resultadoComparador2.getValor();
        if (comparadorSigno1.signo.contains(">") && comparadorSigno2.signo.contains("<")) {
            //es valido
            ComparadorSigno[] arreglo = new ComparadorSigno[2];
            arreglo[0] = comparadorSigno1;
            arreglo[1] = comparadorSigno2;
            return Resultado.ok(arreglo);
        }
        return Resultado.error("error..comparadores no validos");
    }
    public static Resultado<int[]> crearIntervaloFromSubject(String subject){
        String[] data = TecnoUtils.procesarString(subject);
        if (data.length < 2) {
            return Resultado.error("Error se esperaba al menos 2 campos(extremoInferior,extremoSuperior)");
        }
        String extremoInferior = data[0];
        String extremoSuperior = data[1];
        if(extremoSuperior == null || extremoSuperior.equalsIgnoreCase("null")){
            return Resultado.error("error... el extremo superior no puede ser nulo");
        }
        if(extremoInferior == null || extremoInferior.equalsIgnoreCase("null")){
            return Resultado.error("error... el extremo inferior no puede ser nulo");
        }
        int extremoInferiorDTO;
        int extremoSuperiorDTO;
        try{
            extremoInferiorDTO = Integer.parseInt(extremoInferior);
            extremoSuperiorDTO = Integer.parseInt(extremoSuperior);
        }catch (NumberFormatException e){
            return Resultado.error("Error al campos no numericos!");
        }
        if (extremoInferiorDTO > extremoSuperiorDTO) {
            return Resultado.error("Error orden incorrecto el extremo inferior: " + extremoInferiorDTO + "es superior a: " + extremoSuperiorDTO);
        }
        int[] intervalo = new int[2];
        intervalo[0] = extremoInferiorDTO;
        intervalo[1] = extremoSuperiorDTO;
        return Resultado.ok(intervalo);
    }


    public static Resultado<Long[]> obtenerDobleIdFromSubject(String subject){
        String[] data = TecnoUtils.procesarString(subject);
        if (data.length < 2) {
            return Resultado.error("Error se esperaba al menos 2 campos(venta_id,pago_id)");
        }
        String ventaId = data[0];
        String pagoId = data[1];
        if(pagoId == null || pagoId.equalsIgnoreCase("null")){
            return Resultado.error("error... el extremo superior no puede ser nulo");
        }
        if(ventaId == null || ventaId.equalsIgnoreCase("null")){
            return Resultado.error("error... el extremo inferior no puede ser nulo");
        }
        Long ventaIdDto;
        Long pagoIdDto;
        try{
            ventaIdDto = Long.parseLong(ventaId);
            pagoIdDto = Long.parseLong(pagoId);
        }catch (NumberFormatException e){
            return Resultado.error("Error al campos no numericos!");
        }

        Long[] ventaPagos = new Long[2];
        ventaPagos[0] = ventaIdDto;
        ventaPagos[1] = pagoIdDto;
        return Resultado.ok(ventaPagos);
    }
    @Override
    public String toString() {
        return "ComparadorSigno{" +
                "signo='" + signo + '\'' +
                ", valor=" + valor +
                '}';
    }
}
