package com.elahorro.sgpi.repositorio;

import com.elahorro.sgpi.modelo.Usuario;
import com.elahorro.sgpi.modelo.enums.Rol;
import com.elahorro.sgpi.util.CsvUtil;

import java.util.ArrayList;
import java.util.List;

public class UsuarioRepositorio {

    public static final String RUTA = "datos/usuarios.csv";
    private static final String CABECERA = "id;nombre;username;passwordHash;rol";

    public List<Usuario> cargar() {
        List<Usuario> lista = new ArrayList<>();
        for (String[] fila : CsvUtil.leer(RUTA)) {
            lista.add(new Usuario(
                    Integer.parseInt(fila[0]),
                    fila[1],
                    fila[2],
                    fila[3],
                    Rol.valueOf(fila[4])));
        }
        return lista;
    }

    public void guardar(List<Usuario> items) {
        List<String[]> filas = new ArrayList<>();
        for (Usuario usuario : items) {
            filas.add(usuario.toFila());
        }
        CsvUtil.escribir(RUTA, CABECERA, filas);
    }
}
