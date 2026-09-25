package com.elahorro.sgpi.repositorio;

import com.elahorro.sgpi.modelo.Cliente;
import com.elahorro.sgpi.modelo.DetallePedido;
import com.elahorro.sgpi.modelo.Pago;
import com.elahorro.sgpi.modelo.Pedido;
import com.elahorro.sgpi.modelo.Producto;
import com.elahorro.sgpi.modelo.Usuario;
import com.elahorro.sgpi.modelo.enums.EstadoPedido;
import com.elahorro.sgpi.util.CsvUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Persistencia de pedidos y sus detalles en archivos CSV. Necesita las listas
 * de clientes, usuarios, productos y pagos ya cargadas para reconstruir las
 * referencias (relaciones) del dominio.
 */
public class PedidoRepositorio {

    public static final String RUTA = "datos/pedidos.csv";
    public static final String RUTA_DETALLES = "datos/detalles.csv";

    private static final String CABECERA =
            "id;fecha;estado;descuento;total;clienteDni;vendedorUsername;pagoId";
    private static final String CABECERA_DETALLE =
            "pedidoId;detalleId;productoId;cantidad;precioUnitario;subtotal";

    public List<Pedido> cargar(List<Cliente> clientes,
                               List<Usuario> usuarios,
                               List<Producto> productos,
                               List<Pago> pagos) {
        Map<String, Cliente> porDni = new HashMap<>();
        for (Cliente cliente : clientes) {
            porDni.put(cliente.getDni(), cliente);
        }
        Map<String, Usuario> porUsername = new HashMap<>();
        for (Usuario usuario : usuarios) {
            porUsername.put(usuario.getUsername(), usuario);
        }
        Map<Integer, Producto> productoPorId = new HashMap<>();
        for (Producto producto : productos) {
            productoPorId.put(producto.getId(), producto);
        }
        Map<Integer, Pago> pagoPorId = new HashMap<>();
        for (Pago pago : pagos) {
            pagoPorId.put(pago.getId(), pago);
        }

        Map<Integer, List<String[]>> detallesPorPedido = new HashMap<>();
        for (String[] fila : CsvUtil.leer(RUTA_DETALLES)) {
            int pedidoId = Integer.parseInt(fila[0]);
            detallesPorPedido.computeIfAbsent(pedidoId, k -> new ArrayList<>()).add(fila);
        }

        List<Pedido> lista = new ArrayList<>();
        for (String[] fila : CsvUtil.leer(RUTA)) {
            Cliente cliente = porDni.get(fila[5]);
            Usuario vendedor = porUsername.get(fila[6]);
            if (cliente == null || vendedor == null) {
                continue;
            }
            Pedido pedido = new Pedido(Integer.parseInt(fila[0]), cliente, vendedor);
            pedido.setFecha(LocalDate.parse(fila[1]));
            pedido.setDescuentoCargado(Double.parseDouble(fila[3]));
            if (fila.length > 7 && !fila[7].isBlank()) {
                pedido.setPagoCargado(pagoPorId.get(Integer.parseInt(fila[7])));
            }
            List<String[]> listaDetalles = detallesPorPedido.get(pedido.getId());
            if (listaDetalles == null) {
                listaDetalles = new ArrayList<String[]>();
            }
            for (String[] detalle : listaDetalles) {
                Producto producto = productoPorId.get(Integer.parseInt(detalle[2]));
                if (producto == null) {
                    continue;
                }
                pedido.agregarDetalleCargado(new DetallePedido(
                        Integer.parseInt(detalle[1]),
                        producto,
                        Integer.parseInt(detalle[3]),
                        Double.parseDouble(detalle[4])));
            }
            pedido.cambiarEstado(EstadoPedido.valueOf(fila[2]));
            lista.add(pedido);
        }
        return lista;
    }

    public void guardar(List<Pedido> pedidos) {
        List<String[]> filas = new ArrayList<>();
        List<String[]> detalles = new ArrayList<>();
        for (Pedido pedido : pedidos) {
            filas.add(pedido.toFila());
            for (DetallePedido detalle : pedido.getDetalles()) {
                String[] fila = detalle.toFila();
                detalles.add(new String[]{
                        String.valueOf(pedido.getId()),
                        fila[0],
                        fila[1],
                        fila[3],
                        fila[4],
                        fila[5]
                });
            }
        }
        CsvUtil.escribir(RUTA, CABECERA, filas);
        CsvUtil.escribir(RUTA_DETALLES, CABECERA_DETALLE, detalles);
    }
}
