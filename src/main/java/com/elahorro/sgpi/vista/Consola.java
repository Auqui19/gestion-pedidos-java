package com.elahorro.sgpi.vista;

import java.util.Scanner;

/**
 * Utilidades de entrada/salida por consola.
 */
public class Consola {

    private final Scanner scanner = new Scanner(System.in);

    public String leerTexto(String etiqueta) {
        System.out.print(etiqueta);
        return scanner.nextLine().trim();
    }

    public int leerEntero(String etiqueta) {
        int numero = 0;
        boolean valido = false;
        while (!valido) {
            System.out.print(etiqueta);
            String linea = scanner.nextLine().trim();
            try {
                numero = Integer.parseInt(linea);
                valido = true;
            } catch (NumberFormatException e) {
                System.out.println("  Error: ingrese un numero entero valido.");
            }
        }
        return numero;
    }

    public double leerDouble(String etiqueta) {
        double numero = 0.0;
        boolean valido = false;
        while (!valido) {
            System.out.print(etiqueta);
            String linea = scanner.nextLine().trim();
            try {
                numero = Double.parseDouble(linea);
                valido = true;
            } catch (NumberFormatException e) {
                System.out.println("  Error: ingrese un numero valido.");
            }
        }
        return numero;
    }

    public boolean confirmar(String mensaje) {
        System.out.print(mensaje + " (S/N): ");
        String respuesta = scanner.nextLine().trim();
        return respuesta.equalsIgnoreCase("S") || respuesta.equalsIgnoreCase("SI");
    }

    public void pausa() {
        System.out.print("\nPresione ENTER para continuar...");
        scanner.nextLine();
    }

    public void banner() {
        System.out.println("==================================================");
        System.out.println("      TIENDA DE TECNOLOGIA - EL AHORRO");
        System.out.println("  Sistema de Gestion de Pedidos e Inventario");
        System.out.println("                   SGPI");
        System.out.println("==================================================");
    }

    public void titulo(String titulo) {
        System.out.println("\n==================================================");
        System.out.println("  " + titulo);
        System.out.println("==================================================");
    }

    public void separador() {
        System.out.println("--------------------------------------------------");
    }
}
