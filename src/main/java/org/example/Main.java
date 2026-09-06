package org.example;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Producto producto = new Producto("hamburguesa", 12.50);

        System.out.println("=== PRODUCTO REGISTRADO ===");
        producto.mostrarDatos();

        Scanner sc = new Scanner(System.in);

        System.out.println("\n=== REGISTRO DE CLIENTE ===");

        System.out.print("Ingrese nombre: ");
        String nombre = sc.nextLine();

        if (nombre.trim().isEmpty()) {
            System.out.println("Error: El nombre no puede estar vacio.");
            return;
        }

        System.out.print("Ingrese DNI: ");
        String dni = sc.nextLine();

        if (!dni.matches("\\d{8}")) {
            System.out.println("Error: El DNI debe contener exactamente 8 numeros.");
            return;
        }

        Cliente cliente = new Cliente(nombre, dni);

        System.out.println("\nCliente registrado correctamente");
        cliente.mostrarDatos();
    }
}