package com.elahorro.sgpi.servicio;

import com.elahorro.sgpi.modelo.Cliente;
import com.elahorro.sgpi.modelo.DetallePedido;
import com.elahorro.sgpi.modelo.Pago;
import com.elahorro.sgpi.modelo.Pedido;
import com.elahorro.sgpi.modelo.Producto;
import com.elahorro.sgpi.modelo.Usuario;
import com.elahorro.sgpi.modelo.enums.MetodoPago;
import com.elahorro.sgpi.repositorio.PagoRepositorio;
import com.elahorro.sgpi.repositorio.PedidoRepositorio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PedidoService {

    private final PedidoRepositorio repositorio;
    private final ProductoService productos;
    private final PagoRepositorio pagoRepositorio;
    private final List<Pedido> pedidos;
    private final List<Pago> pagos;

    public PedidoService(PedidoRepositorio repositorio,
                         ProductoService productos,
                         PagoRepositorio pagoRepositorio,
                         ClienteService clientes,
                         UsuarioService usuarios) {
        this.repositorio = repositorio;
        this.productos = productos;
        this.pagoRepositorio = pagoRepositorio;
        this.pagos = new ArrayList<Pago>(pagoRepositorio.cargar());
        this.pedidos = new ArrayList<Pedido>(repositorio.cargar(
                clientes.listar(), usuarios.listar(), productos.listar(), pagos));
    }

    public Pedido crear(Cliente cliente, Usuario vendedor) {
        Pedido pedido = new Pedido(siguienteId(), cliente, vendedor);
        pedidos.add(pedido);
        guardar();
        return pedido;
    }

    public DetallePedido agregarProducto(Pedido pedido, Producto producto, int cantidad) {
        DetallePedido detalle = pedido.agregarDetalle(producto, cantidad);
        productos.guardar();
        guardar();
        return detalle;
    }

    public void aplicarDescuento(Pedido pedido, double descuento, boolean autorizado) {
        pedido.setDescuento(descuento, autorizado);
        guardar();
    }

    public Pago registrarPago(Pedido pedido, MetodoPago metodo) {
        Pago pago = new Pago(siguientePagoId(), LocalDate.now(), pedido.getTotal(), metodo);
        pedido.pagar(pago);
        pagos.add(pago);
        pagoRepositorio.guardar(pagos);
        guardar();
        return pago;
    }

    public void confirmar(Pedido pedido) {
        pedido.confirmar();
        guardar();
    }

    public void cancelar(Pedido pedido) {
        pedido.cancelar();
        productos.guardar();
        guardar();
    }

    public List<Pedido> listar() {
        return new ArrayList<Pedido>(pedidos);
    }

    public Pedido buscarPorId(int id) {
        for (int i = 0; i < pedidos.size(); i++) {
            if (pedidos.get(i).getId() == id) {
                return pedidos.get(i);
            }
        }
        return null;
    }

    public int siguienteId() {
        int max = 0;
        for (int i = 0; i < pedidos.size(); i++) {
            if (pedidos.get(i).getId() > max) {
                max = pedidos.get(i).getId();
            }
        }
        return max + 1;
    }

    public int siguientePagoId() {
        int max = 0;
        for (int i = 0; i < pagos.size(); i++) {
            if (pagos.get(i).getId() > max) {
                max = pagos.get(i).getId();
            }
        }
        return max + 1;
    }

    public void guardar() {
        repositorio.guardar(pedidos);
    }
}
