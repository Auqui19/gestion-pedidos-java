package org.example;

import org.example.Producto;

public class Pedido {

    private Producto producto;
    private int cantidad;

    public Pedido(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public void mostrarPedido() {
        System.out.println("Producto: " + producto.getNombre());
        System.out.println("Cantidad: " + cantidad);
    }
}