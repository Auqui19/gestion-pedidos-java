package com.elahorro.sgpi.servicio;

import com.elahorro.sgpi.modelo.Cliente;
import com.elahorro.sgpi.modelo.DetallePedido;
import com.elahorro.sgpi.modelo.Pago;
import com.elahorro.sgpi.modelo.Pedido;
import com.elahorro.sgpi.modelo.Producto;
import com.elahorro.sgpi.modelo.Usuario;
import com.elahorro.sgpi.modelo.enums.MetodoPago;
import com.elahorro.sgpi.repositorio.PagoDAO;
import com.elahorro.sgpi.repositorio.PedidoDAO;
import com.elahorro.sgpi.repositorio.ProductoDAO;

import java.time.LocalDate;
import java.util.List;

public class PedidoService {

    private final PedidoDAO dao;
    private final ProductoDAO productoDAO;
    private final PagoDAO pagoDAO;
    private final ProductoService productos;

    public PedidoService(PedidoDAO dao,
                         ProductoService productos,
                         ProductoDAO productoDAO,
                         PagoDAO pagoDAO) {
        this.dao = dao;
        this.productos = productos;
        this.productoDAO = productoDAO;
        this.pagoDAO = pagoDAO;
    }

    public Pedido crear(Cliente cliente, Usuario vendedor) {
        Pedido pedido = new Pedido(siguienteId(), cliente, vendedor);
        dao.insertar(pedido);
        return pedido;
    }

    public DetallePedido agregarProducto(Pedido pedido, Producto producto, int cantidad) {
        DetallePedido detalle = pedido.agregarDetalle(producto, cantidad);
        productoDAO.actualizarStock(producto);
        dao.actualizar(pedido);
        return detalle;
    }

    public void aplicarDescuento(Pedido pedido, double descuento, boolean autorizado) {
        pedido.setDescuento(descuento, autorizado);
        dao.actualizar(pedido);
    }

    public Pago registrarPago(Pedido pedido, MetodoPago metodo) {
        Pago pago = new Pago(siguientePagoId(), LocalDate.now(), pedido.getTotal(), metodo);
        pagoDAO.insertar(pago);
        pedido.pagar(pago);
        dao.actualizar(pedido);
        return pago;
    }

    public void confirmar(Pedido pedido) {
        pedido.confirmar();
        dao.actualizar(pedido);
    }

    public void cancelar(Pedido pedido) {
        pedido.cancelar();
        for (DetallePedido detalle : pedido.getDetalles()) {
            productoDAO.actualizarStock(detalle.getProducto());
        }
        dao.actualizar(pedido);
    }

    public List<Pedido> listar() {
        return dao.listar();
    }

    public Pedido buscarPorId(int id) {
        return dao.buscarPorId(id).orElse(null);
    }

    public int siguienteId() {
        int max = 0;
        List<Pedido> pedidos = dao.listar();
        for (int i = 0; i < pedidos.size(); i++) {
            if (pedidos.get(i).getId() > max) {
                max = pedidos.get(i).getId();
            }
        }
        return max + 1;
    }

    public int siguientePagoId() {
        int max = 0;
        List<Pago> pagos = pagoDAO.listar();
        for (int i = 0; i < pagos.size(); i++) {
            if (pagos.get(i).getId() > max) {
                max = pagos.get(i).getId();
            }
        }
        return max + 1;
    }
}
