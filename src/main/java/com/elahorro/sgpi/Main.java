package com.elahorro.sgpi;

import com.elahorro.sgpi.servicio.Sistema;
import com.elahorro.sgpi.vista.AplicacionConsola;

/**
 * Punto de entrada del sistema SGPI (version de consola).
 * Usuario por defecto: admin / admin123
 */
public class Main {

    public static void main(String[] args) {
        Sistema sistema = new Sistema();
        new AplicacionConsola(sistema).iniciar();
    }
}
