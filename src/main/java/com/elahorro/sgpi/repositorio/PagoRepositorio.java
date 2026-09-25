package com.elahorro.sgpi.repositorio;

import com.elahorro.sgpi.modelo.Pago;
import com.elahorro.sgpi.modelo.enums.MetodoPago;
import com.elahorro.sgpi.util.CsvUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PagoRepositorio {

    public static final String RUTA = "datos/pagos.csv";
    private static final String CABECERA = "id;fecha;monto;metodo";

    public List<Pago> cargar() {
        List<Pago> lista = new ArrayList<>();
        for (String[] fila : CsvUtil.leer(RUTA)) {
            lista.add(new Pago(
                    Integer.parseInt(fila[0]),
                    LocalDate.parse(fila[1]),
                    Double.parseDouble(fila[2]),
                    MetodoPago.valueOf(fila[3])));
        }
        return lista;
    }

    public void guardar(List<Pago> items) {
        List<String[]> filas = new ArrayList<>();
        for (Pago pago : items) {
            filas.add(pago.toFila());
        }
        CsvUtil.escribir(RUTA, CABECERA, filas);
    }
}
