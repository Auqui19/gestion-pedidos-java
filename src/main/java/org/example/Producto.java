package org.example;

public class Producto {

    private String nombre;
    private double precio;

    public Producto(String nombre, double precio) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacio.");
        }
        if (!precioValido(precio)) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        this.nombre = nombre;
        this.precio = precio;
    }

    public Producto(String nombre) {
        this(nombre, 0);
    }

    private boolean precioValido(double precio) {
        return precio >= 0;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void mostrarDatos() {
        System.out.println("Producto: " + nombre);
        System.out.printf("Precio: S/ %.2f%n", precio);
    }

    public void mostrarDatos(boolean mostrarPrecio) {
        System.out.println("Producto: " + nombre);
        if (mostrarPrecio) {
            System.out.printf("Precio: S/ %.2f%n", precio);
        }
    }
}