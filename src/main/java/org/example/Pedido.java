package org.example;

import org.example.Producto;

public class Pedido {

    private Producto producto;
    private int cantidad;

    public Pedido(Producto producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("El pedido requiere un producto.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        }
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double calcularTotal() {
        return producto.getPrecio() * cantidad;
    }

    public void mostrarPedido() {
        System.out.println("Producto: " + producto.getNombre());
        System.out.println("Cantidad: " + cantidad);
        System.out.printf("Precio unitario: S/ %.2f%n", producto.getPrecio());
        System.out.printf("Importe total: S/ %.2f%n", calcularTotal());
    }
}