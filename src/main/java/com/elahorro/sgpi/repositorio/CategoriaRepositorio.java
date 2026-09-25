package com.elahorro.sgpi.repositorio;

import com.elahorro.sgpi.modelo.Categoria;
import com.elahorro.sgpi.util.CsvUtil;

import java.util.ArrayList;
import java.util.List;

public class CategoriaRepositorio {

    public static final String RUTA = "datos/categorias.csv";
    private static final String CABECERA = "id;nombre";

    public List<Categoria> cargar() {
        List<Categoria> lista = new ArrayList<>();
        for (String[] fila : CsvUtil.leer(RUTA)) {
            lista.add(new Categoria(
                    Integer.parseInt(fila[0]),
                    fila[1]));
        }
        return lista;
    }

    public void guardar(List<Categoria> items) {
        List<String[]> filas = new ArrayList<>();
        for (Categoria categoria : items) {
            filas.add(categoria.toFila());
        }
        CsvUtil.escribir(RUTA, CABECERA, filas);
    }
}
