package org.example;

public class Cliente {

    private String nombre;
    private String dni;

    public Cliente(String nombre, String dni) {
        this.nombre = nombre;
        this.dni = dni;
    }

    public void mostrarDatos() {
        System.out.println("Nombre: " + nombre);
        System.out.println("DNI: " + dni);
    }
}