package com.elahorro.sgpi.modelo;

import com.elahorro.sgpi.util.Validador;

/**
 * Categoria que clasifica a los productos del inventario.
 */
public class Categoria implements Identificable, Mostrable {

    private int id;
    private String nombre;

    public Categoria(int id, String nombre) {
        this.id = id;
        setNombre(nombre);
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        Validador.longitudMinima(nombre, 3, "nombre de categoria");
        this.nombre = nombre.trim();
    }

    @Override
    public String[] toFila() {
        return new String[]{String.valueOf(id), nombre};
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Categoria)) {
            return false;
        }
        Categoria otra = (Categoria) obj;
        return id != 0 && id == otra.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
