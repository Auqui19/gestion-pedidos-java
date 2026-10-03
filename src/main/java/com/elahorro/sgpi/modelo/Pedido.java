package com.elahorro.sgpi.modelo;

import com.elahorro.sgpi.modelo.enums.EstadoPedido;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pedido del minimarket. Contiene varios detalles (composicion), tiene un
 * cliente obligatorio (RN-03) y un pago asociado. Controla el stock (RN-02)
 * y el descuento maximo sin autorizacion (RN-06).
 */
public class Pedido implements Identificable, Mostrable {

    public static final double DESCUENTO_MAX_SIN_AUTORIZACION = 20.0;

    private int id;
    private LocalDate fecha;
    private EstadoPedido estado;
    private double descuento;
    private double total;
    private Cliente cliente;
    private Usuario vendedor;
    private Pago pago;
    private final List<DetallePedido> detalles;

    public Pedido(int id, Cliente cliente, Usuario vendedor) {
        if (cliente == null) {
            throw new IllegalArgumentException("El pedido requiere un cliente (RN-03).");
        }
        if (vendedor == null) {
            throw new IllegalArgumentException("El pedido requiere un usuario vendedor.");
        }
        this.id = id;
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.fecha = LocalDate.now();
        this.estado = EstadoPedido.PENDIENTE;
        this.descuento = 0.0;
        this.total = 0.0;
        this.detalles = new ArrayList<>();
    }

    public DetallePedido agregarDetalle(Producto producto, int cantidad) {
        if (estado != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se pueden agregar productos a pedidos en estado Pendiente.");
        }
        if (producto == null) {
            throw new IllegalArgumentException("El producto es obligatorio.");
        }
        producto.reducirStock(cantidad);
        DetallePedido detalle = new DetallePedido(
                siguienteIdDetalle(), producto, cantidad, producto.getPrecio());
        detalles.add(detalle);
        recalcularTotal();
        return detalle;
    }

    public void setDescuento(double descuento, boolean autorizado) {
        if (descuento < 0 || descuento > 100) {
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 100.");
        }
        if (descuento > DESCUENTO_MAX_SIN_AUTORIZACION && !autorizado) {
            throw new IllegalArgumentException(
                    "El descuento maximo sin autorizacion es "
                            + (int) DESCUENTO_MAX_SIN_AUTORIZACION + "% (RN-06).");
        }
        this.descuento = descuento;
        recalcularTotal();
    }

    private int siguienteIdDetalle() {
        int max = 0;
        for (DetallePedido d : detalles) {
            max = Math.max(max, d.getId());
        }
        return max + 1;
    }

    public void recalcularTotal() {
        double suma = 0.0;
        for (DetallePedido detalle : detalles) {
            suma += detalle.getSubtotal();
        }
        this.total = redondear(suma * (1 - descuento / 100.0));
    }

    public double calcularTotal() {
        recalcularTotal();
        return total;
    }

    public void confirmar() {
        if (detalles.isEmpty()) {
            throw new IllegalStateException("No se puede confirmar un pedido sin productos.");
        }
        this.estado = EstadoPedido.CONFIRMADO;
    }

    public void pagar(Pago pago) {
        if (pago == null) {
            throw new IllegalArgumentException("El pago es obligatorio.");
        }
        if (detalles.isEmpty()) {
            throw new IllegalStateException("No se puede pagar un pedido sin productos.");
        }
        if (Math.abs(pago.getMonto() - total) > 0.01) {
            throw new IllegalArgumentException(
                    "El monto del pago (S/ " + String.format("%.2f", pago.getMonto())
                            + ") debe ser igual al total del pedido (S/ "
                            + String.format("%.2f", total) + ").");
        }
        this.pago = pago;
        this.estado = EstadoPedido.PAGADO;
    }

    public void cancelar() {
        if (estado == EstadoPedido.CANCELADO) {
            return;
        }
        for (DetallePedido detalle : detalles) {
            detalle.getProducto().aumentarStock(detalle.getCantidad());
        }
        this.estado = EstadoPedido.CANCELADO;
    }

    public void cambiarEstado(EstadoPedido nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado es obligatorio.");
        }
        this.estado = nuevoEstado;
    }

    // --- Metodos de uso interno para la carga desde archivos CSV ---

    public void agregarDetalleCargado(DetallePedido detalle) {
        detalles.add(detalle);
        recalcularTotal();
    }

    public void setDescuentoCargado(double descuento) {
        this.descuento = descuento;
        recalcularTotal();
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setPagoCargado(Pago pago) {
        this.pago = pago;
    }

    // --- Getters ---

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public double getDescuento() {
        return descuento;
    }

    public double getTotal() {
        return total;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Usuario getVendedor() {
        return vendedor;
    }

    public Pago getPago() {
        return pago;
    }

    public List<DetallePedido> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    @Override
    public String[] toFila() {
        return new String[]{
                String.valueOf(id),
                fecha.toString(),
                estado.name(),
                String.format("%.2f", descuento),
                String.format("%.2f", total),
                cliente.getDni(),
                vendedor.getUsername(),
                pago == null ? "" : String.valueOf(pago.getId())
        };
    }

    @Override
    public String toString() {
        return "Pedido #" + id + " - " + cliente.getNombre()
                + " - " + estado + " - S/ " + String.format("%.2f", total);
    }
}
