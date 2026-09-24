package com.elahorro.sgpi.repositorio;

import com.elahorro.sgpi.modelo.Categoria;
import com.elahorro.sgpi.modelo.Producto;
import com.elahorro.sgpi.util.CsvUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductoRepositorio {

    public static final String RUTA = "datos/productos.csv";
    private static final String CABECERA = "id;codigo;nombre;precio;stock;stockMinimo;categoriaId";

    private final CategoriaRepositorio categoriaRepositorio;

    public ProductoRepositorio(CategoriaRepositorio categoriaRepositorio) {
        this.categoriaRepositorio = categoriaRepositorio;
    }

    public List<Producto> cargar() {
        Map<Integer, Categoria> categorias = new HashMap<>();
        for (Categoria categoria : categoriaRepositorio.cargar()) {
            categorias.put(categoria.getId(), categoria);
        }

        List<Producto> lista = new ArrayList<>();
        for (String[] fila : CsvUtil.leer(RUTA)) {
            Categoria categoria = categorias.get(Integer.parseInt(fila[6]));
            if (categoria == null) {
                continue;
            }
            lista.add(new Producto(
                    Integer.parseInt(fila[0]),
                    fila[1],
                    fila[2],
                    Double.parseDouble(fila[3]),
                    Integer.parseInt(fila[4]),
                    Integer.parseInt(fila[5]),
                    categoria));
        }
        return lista;
    }

    public void guardar(List<Producto> items) {
        List<String[]> filas = new ArrayList<>();
        for (Producto producto : items) {
            filas.add(producto.toFila());
        }
        CsvUtil.escribir(RUTA, CABECERA, filas);
    }
}
