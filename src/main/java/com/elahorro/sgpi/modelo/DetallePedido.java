package com.elahorro.sgpi.modelo;

import com.elahorro.sgpi.util.Validador;

/**
 * Detalle de un pedido. El precio unitario queda congelado al momento
 * de la venta (inmutabilidad, RN-05).
 */
public class DetallePedido implements Identificable, Mostrable {

    private final int id;
    private final Producto producto;
    private final int cantidad;
    private final double precioUnitario;
    private final double subtotal;

    public DetallePedido(int id, Producto producto, int cantidad, double precioUnitario) {
        if (producto == null) {
            throw new IllegalArgumentException("El detalle requiere un producto.");
        }
        Validador.enteroPositivo(cantidad, "cantidad");
        Validador.numeroPositivo(precioUnitario, "precio unitario");
        this.id = id;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = calcularSubtotal();
    }

    @Override
    public int getId() {
        return id;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double calcularSubtotal() {
        return cantidad * precioUnitario;
    }

    @Override
    public String[] toFila() {
        return new String[]{
                String.valueOf(id),
                String.valueOf(producto.getId()),
                producto.getNombre(),
                String.valueOf(cantidad),
                String.format("%.2f", precioUnitario),
                String.format("%.2f", subtotal)
        };
    }
}
