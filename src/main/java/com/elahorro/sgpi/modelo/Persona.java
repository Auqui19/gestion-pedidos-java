package com.elahorro.sgpi.modelo;

import com.elahorro.sgpi.util.Validador;

/**
 * Clase base abstracta que representa a cualquier persona del dominio
 * (usuarios del sistema y clientes de la tienda).
 * Aplica herencia, abstraccion y encapsulamiento.
 */
public abstract class Persona implements Identificable {

    private int id;
    private String nombre;

    protected Persona(int id, String nombre) {
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
        Validador.longitudMinima(nombre, 3, "nombre");
        this.nombre = nombre.trim();
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
        if (!(obj instanceof Persona)) {
            return false;
        }
        Persona otra = (Persona) obj;
        return id != 0 && id == otra.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
