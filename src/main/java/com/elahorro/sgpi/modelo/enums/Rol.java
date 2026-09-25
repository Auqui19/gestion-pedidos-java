package com.elahorro.sgpi.modelo.enums;

public enum Rol {

    ADMINISTRADOR("Administrador"),
    VENDEDOR("Vendedor"),
    ALMACENERO("Almacenero");

    private final String descripcion;

    Rol(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
