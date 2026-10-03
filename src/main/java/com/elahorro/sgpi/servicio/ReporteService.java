package com.elahorro.sgpi.servicio;

import com.elahorro.sgpi.modelo.Pedido;
import com.elahorro.sgpi.modelo.Producto;
import com.elahorro.sgpi.modelo.enums.EstadoPedido;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Reportes de inventario y ventas. Los datos se consultan a la base de datos
 * a traves de los servicios; ya no se exportan a CSV.
 */
public class ReporteService {

    private final PedidoService pedidos;
    private final ProductoService productos;

    public ReporteService(PedidoService pedidos, ProductoService productos) {
        this.pedidos = pedidos;
        this.productos = productos;
    }

    public List<Producto> stockBajo() {
        return productos.listarStockBajo();
    }

    public List<Pedido> ventasPorFecha(LocalDate desde, LocalDate hasta) {
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException(
                    "La fecha inicial no puede ser posterior a la fecha final.");
        }
        List<Pedido> encontrados = new ArrayList<Pedido>();
        List<Pedido> todos = pedidos.listar();
        for (int i = 0; i < todos.size(); i++) {
            Pedido pedido = todos.get(i);
            if (pedido.getEstado() != EstadoPedido.PAGADO) {
                continue;
            }
            if (pedido.getFecha().isBefore(desde) || pedido.getFecha().isAfter(hasta)) {
                continue;
            }
            encontrados.add(pedido);
        }
        return encontrados;
    }

    public double totalVentas(LocalDate desde, LocalDate hasta) {
        double total = 0.0;
        List<Pedido> ventas = ventasPorFecha(desde, hasta);
        for (int i = 0; i < ventas.size(); i++) {
            total = total + ventas.get(i).getTotal();
        }
        return total;
    }
}
