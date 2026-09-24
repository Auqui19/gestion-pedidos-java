package com.elahorro.sgpi.modelo;

public interface Mostrable {

    String[] toFila();

    default String cabecera() {
        return getClass().getSimpleName();
    }
}
