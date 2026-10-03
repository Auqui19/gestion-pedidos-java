package com.elahorro.sgpi.repositorio;

import com.elahorro.sgpi.config.Conexion;
import com.elahorro.sgpi.modelo.Cliente;
import com.elahorro.sgpi.modelo.DetallePedido;
import com.elahorro.sgpi.modelo.Pago;
import com.elahorro.sgpi.modelo.Pedido;
import com.elahorro.sgpi.modelo.Producto;
import com.elahorro.sgpi.modelo.Usuario;
import com.elahorro.sgpi.modelo.enums.EstadoPedido;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a las tablas `pedidos` y `detalles` mediante JDBC. Reconstruye las
 * referencias (cliente, vendedor, producto, pago) con maps.
 *
 * Asume que las listas de clientes, usuarios, productos y pagos ya estan
 * cargadas por sus respectivos DAO (el orden lo garantiza {@code Sistema}).
 */
public class PedidoDAO implements Repositorio<Pedido> {

    private final ClienteDAO clienteDAO;
    private final UsuarioDAO usuarioDAO;
    private final ProductoDAO productoDAO;
    private final PagoDAO pagoDAO;

    public PedidoDAO(ClienteDAO clienteDAO, UsuarioDAO usuarioDAO,
                     ProductoDAO productoDAO, PagoDAO pagoDAO) {
        this.clienteDAO = clienteDAO;
        this.usuarioDAO = usuarioDAO;
        this.productoDAO = productoDAO;
        this.pagoDAO = pagoDAO;
    }

    @Override
    public List<Pedido> listar() {
        MapasReferencia ref = cargarReferencias();
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT id, fecha, estado, descuento, total, cliente_id, vendedor_id, "
                + "pago_id FROM pedidos ORDER BY id";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Cliente cliente = ref.clientes.get(rs.getInt("cliente_id"));
                Usuario vendedor = ref.usuarios.get(rs.getInt("vendedor_id"));
                if (cliente == null || vendedor == null) {
                    continue;
                }
                Pedido pedido = new Pedido(rs.getInt("id"), cliente, vendedor);
                pedido.setFecha(rs.getDate("fecha").toLocalDate());
                pedido.setDescuentoCargado(rs.getBigDecimal("descuento").doubleValue());
                int pagoId = rs.getInt("pago_id");
                if (!rs.wasNull()) {
                    Pago pago = ref.pagos.get(pagoId);
                    if (pago != null) {
                        pedido.setPagoCargado(pago);
                    }
                }
                pedido.cambiarEstado(EstadoPedido.valueOf(rs.getString("estado")));
                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al listar pedidos.", e);
        }

        cargarDetalles(pedidos, ref.productos);
        return pedidos;
    }

    private void cargarDetalles(List<Pedido> pedidos, java.util.Map<Integer, Producto> productos) {
        java.util.Map<Integer, Pedido> porId = new java.util.HashMap<>();
        for (Pedido pedido : pedidos) {
            porId.put(pedido.getId(), pedido);
        }
        String sql = "SELECT pedido_id, numero, producto_id, cantidad, precio_unitario "
                + "FROM detalles ORDER BY pedido_id, numero";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Pedido pedido = porId.get(rs.getInt("pedido_id"));
                Producto producto = productos.get(rs.getInt("producto_id"));
                if (pedido == null || producto == null) {
                    continue;
                }
                DetallePedido detalle = new DetallePedido(
                        rs.getInt("numero"),
                        producto,
                        rs.getInt("cantidad"),
                        rs.getBigDecimal("precio_unitario").doubleValue());
                pedido.agregarDetalleCargado(detalle);
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al cargar los detalles de pedidos.", e);
        }
    }

    private MapasReferencia cargarReferencias() {
        MapasReferencia ref = new MapasReferencia();
        for (Cliente cliente : clienteDAO.listar()) {
            ref.clientes.put(cliente.getId(), cliente);
        }
        for (Usuario usuario : usuarioDAO.listar()) {
            ref.usuarios.put(usuario.getId(), usuario);
        }
        for (Producto producto : productoDAO.listar()) {
            ref.productos.put(producto.getId(), producto);
        }
        for (Pago pago : pagoDAO.listar()) {
            ref.pagos.put(pago.getId(), pago);
        }
        return ref;
    }

    @Override
    public void insertar(Pedido pedido) {
        String sql = "INSERT INTO pedidos "
                + "(id, fecha, estado, descuento, total, cliente_id, vendedor_id, pago_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            escribirPedido(ps, pedido);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al insertar el pedido.", e);
        }
        guardarDetalles(pedido);
    }

    @Override
    public void actualizar(Pedido pedido) {
        String sql = "UPDATE pedidos SET fecha = ?, estado = ?, descuento = ?, total = ?, "
                + "cliente_id = ?, vendedor_id = ?, pago_id = ? WHERE id = ?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(pedido.getFecha()));
            ps.setString(2, pedido.getEstado().name());
            ps.setBigDecimal(3, BigDecimal.valueOf(pedido.getDescuento()));
            ps.setBigDecimal(4, BigDecimal.valueOf(pedido.getTotal()));
            ps.setInt(5, pedido.getCliente().getId());
            ps.setInt(6, pedido.getVendedor().getId());
            if (pedido.getPago() == null) {
                ps.setNull(7, java.sql.Types.INTEGER);
            } else {
                ps.setInt(7, pedido.getPago().getId());
            }
            ps.setInt(8, pedido.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al actualizar el pedido.", e);
        }
        guardarDetalles(pedido);
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM pedidos WHERE id = ?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al eliminar el pedido.", e);
        }
    }

    private void escribirPedido(PreparedStatement ps, Pedido pedido) throws SQLException {
        ps.setInt(1, pedido.getId());
        ps.setDate(2, Date.valueOf(pedido.getFecha()));
        ps.setString(3, pedido.getEstado().name());
        ps.setBigDecimal(4, BigDecimal.valueOf(pedido.getDescuento()));
        ps.setBigDecimal(5, BigDecimal.valueOf(pedido.getTotal()));
        ps.setInt(6, pedido.getCliente().getId());
        ps.setInt(7, pedido.getVendedor().getId());
        if (pedido.getPago() == null) {
            ps.setNull(8, java.sql.Types.INTEGER);
        } else {
            ps.setInt(8, pedido.getPago().getId());
        }
    }

    private void guardarDetalles(Pedido pedido) {
        String sql = "INSERT INTO detalles "
                + "(pedido_id, numero, producto_id, cantidad, precio_unitario, subtotal) "
                + "VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE "
                + "producto_id = VALUES(producto_id), cantidad = VALUES(cantidad), "
                + "precio_unitario = VALUES(precio_unitario), subtotal = VALUES(subtotal)";
        try (Connection cn = Conexion.getConnection()) {
            cn.setAutoCommit(false);
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                for (DetallePedido detalle : pedido.getDetalles()) {
                    ps.setInt(1, pedido.getId());
                    ps.setInt(2, detalle.getId());
                    ps.setInt(3, detalle.getProducto().getId());
                    ps.setInt(4, detalle.getCantidad());
                    ps.setBigDecimal(5, BigDecimal.valueOf(detalle.getPrecioUnitario()));
                    ps.setBigDecimal(6, BigDecimal.valueOf(detalle.getSubtotal()));
                    ps.addBatch();
                }
                ps.executeBatch();
                cn.commit();
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            } finally {
                cn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al guardar los detalles del pedido.", e);
        }
    }

    private static final class MapasReferencia {
        private final java.util.Map<Integer, Cliente> clientes = new java.util.HashMap<>();
        private final java.util.Map<Integer, Usuario> usuarios = new java.util.HashMap<>();
        private final java.util.Map<Integer, Producto> productos = new java.util.HashMap<>();
        private final java.util.Map<Integer, Pago> pagos = new java.util.HashMap<>();
    }
}
