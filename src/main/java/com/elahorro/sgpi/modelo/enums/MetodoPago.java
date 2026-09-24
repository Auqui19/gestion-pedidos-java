package com.elahorro.sgpi.modelo.enums;

public enum MetodoPago {

    EFECTIVO("Efectivo"),
    TARJETA("Tarjeta"),
    YAPE("Yape"),
    PLIN("Plin"),
    TRANSFERENCIA("Transferencia");

    private final String descripcion;

    MetodoPago(String descripcion) {
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
