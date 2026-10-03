package com.elahorro.sgpi.repositorio;

/**
 * Error no controlado producido al acceder a la base de datos.
 * Envuelve la SQLException para no obligar a la vista a manejarla.
 */
public class AccesoDatosException extends RuntimeException {

    public AccesoDatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public AccesoDatosException(String mensaje) {
        super(mensaje);
    }
}
