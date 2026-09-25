package com.elahorro.sgpi.repositorio;

import com.elahorro.sgpi.modelo.Cliente;
import com.elahorro.sgpi.util.CsvUtil;

import java.util.ArrayList;
import java.util.List;

public class ClienteRepositorio {

    public static final String RUTA = "datos/clientes.csv";
    private static final String CABECERA = "id;nombre;dni;telefono;direccion";

    public List<Cliente> cargar() {
        List<Cliente> lista = new ArrayList<>();
        for (String[] fila : CsvUtil.leer(RUTA)) {
            lista.add(new Cliente(
                    Integer.parseInt(fila[0]),
                    fila[1],
                    fila[2],
                    fila[3],
                    fila[4]));
        }
        return lista;
    }

    public void guardar(List<Cliente> items) {
        List<String[]> filas = new ArrayList<>();
        for (Cliente cliente : items) {
            filas.add(cliente.toFila());
        }
        CsvUtil.escribir(RUTA, CABECERA, filas);
    }
}
