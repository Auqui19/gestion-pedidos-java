package com.elahorro.sgpi.util;

public final class Validador {

    private Validador() {
    }

    public static void textoObligatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio.");
        }
    }

    public static void longitudMinima(String valor, int minimo, String campo) {
        textoObligatorio(valor, campo);
        if (valor.trim().length() < minimo) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " debe tener al menos " + minimo + " caracteres.");
        }
    }

    public static void numeroPositivo(double valor, String campo) {
        if (Double.isNaN(valor) || Double.isInfinite(valor) || valor <= 0) {
            throw new IllegalArgumentException("El campo " + campo + " debe ser mayor a 0.");
        }
    }

    public static void noNegativo(double valor, String campo) {
        if (Double.isNaN(valor) || Double.isInfinite(valor) || valor < 0) {
            throw new IllegalArgumentException("El campo " + campo + " no puede ser negativo.");
        }
    }

    public static void enteroPositivo(int valor, String campo) {
        if (valor <= 0) {
            throw new IllegalArgumentException("El campo " + campo + " debe ser mayor a 0.");
        }
    }

    public static void enteroNoNegativo(int valor, String campo) {
        if (valor < 0) {
            throw new IllegalArgumentException("El campo " + campo + " no puede ser negativo.");
        }
    }

    public static void dni(String dni) {
        if (dni == null || !dni.trim().matches("\\d{8}")) {
            throw new IllegalArgumentException("El DNI debe contener exactamente 8 digitos.");
        }
    }
}
