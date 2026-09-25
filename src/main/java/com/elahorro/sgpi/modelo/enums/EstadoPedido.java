package com.elahorro.sgpi.modelo.enums;

public enum EstadoPedido {

    PENDIENTE("Pendiente"),
    CONFIRMADO("Confirmado"),
    PAGADO("Pagado"),
    CANCELADO("Cancelado");

    private final String descripcion;

    EstadoPedido(String descripcion) {
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
