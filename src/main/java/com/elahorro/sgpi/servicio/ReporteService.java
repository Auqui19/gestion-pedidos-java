package com.elahorro.sgpi.servicio;

import com.elahorro.sgpi.modelo.Pedido;
import com.elahorro.sgpi.modelo.Producto;
import com.elahorro.sgpi.modelo.enums.EstadoPedido;
import com.elahorro.sgpi.util.CsvUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public void exportarStockBajo(String ruta) {
        List<String[]> filas = new ArrayList<String[]>();
        List<Producto> lista = stockBajo();
        for (int i = 0; i < lista.size(); i++) {
            Producto producto = lista.get(i);
            filas.add(new String[]{
                    producto.getCodigo(),
                    producto.getNombre(),
                    String.valueOf(producto.getStock()),
                    String.valueOf(producto.getStockMinimo()),
                    producto.getCategoria().getNombre()
            });
        }
        CsvUtil.escribir(ruta, "codigo;nombre;stock;stockMinimo;categoria", filas);
    }

    public void exportarVentas(String ruta, LocalDate desde, LocalDate hasta) {
        List<String[]> filas = new ArrayList<String[]>();
        List<Pedido> lista = ventasPorFecha(desde, hasta);
        for (int i = 0; i < lista.size(); i++) {
            Pedido pedido = lista.get(i);
            filas.add(new String[]{
                    String.valueOf(pedido.getId()),
                    pedido.getFecha().toString(),
                    pedido.getCliente().getNombre(),
                    pedido.getCliente().getDni(),
                    pedido.getVendedor().getUsername(),
                    String.format("%.2f", pedido.getTotal())
            });
        }
        CsvUtil.escribir(ruta, "id;fecha;cliente;dni;vendedor;total", filas);
    }
}
