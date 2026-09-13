package org.example;

public class Producto {
    private String nombre;
    private double precio;

    public Producto(String nombre, double precio) {
        validarNombre(nombre);
        validarPrecio(precio);
        this.nombre = nombre.trim();
        this.precio = precio;
    }

    public Producto(String nombre) {
        this(nombre, 0); // <-- punto a decidir abajo
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El nombre del producto no puede estar vacío."
            );
        }
        if (nombre.trim().length() < 3) {
            throw new IllegalArgumentException(
                "El nombre del producto debe tener al menos 3 caracteres."
            );
        }
    }

    private void validarPrecio(double precio) {
        if (Double.isNaN(precio) || Double.isInfinite(precio)) {
            throw new IllegalArgumentException(
                "El precio debe ser un número válido."
            );
        }
        if (precio < 0) { // o <= 0 según elijas
            throw new IllegalArgumentException(
                "El precio no puede ser negativo."
            );
        }
    }

    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }

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