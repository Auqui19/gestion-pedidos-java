package com.elahorro.sgpi.repositorio;

import com.elahorro.sgpi.config.Conexion;
import com.elahorro.sgpi.modelo.Categoria;
import com.elahorro.sgpi.modelo.Producto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla `productos` mediante JDBC. Reconstruye la relacion con
 * `categorias` con un JOIN.
 */
public class ProductoDAO implements Repositorio<Producto> {

    private static final String SELECT_BASE =
            "SELECT p.id, p.codigo, p.nombre, p.precio, p.stock, p.stock_minimo, "
                    + "c.id AS categoria_id, c.nombre AS categoria_nombre "
                    + "FROM productos p JOIN categorias c ON c.id = p.categoria_id ";

    @Override
    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT_BASE + "ORDER BY p.codigo");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al listar productos.", e);
        }
        return lista;
    }

    @Override
    public void insertar(Producto producto) {
        String sql = "INSERT INTO productos "
                + "(id, codigo, nombre, precio, stock, stock_minimo, categoria_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, producto.getId());
            ps.setString(2, producto.getCodigo());
            ps.setString(3, producto.getNombre());
            ps.setBigDecimal(4, BigDecimal.valueOf(producto.getPrecio()));
            ps.setInt(5, producto.getStock());
            ps.setInt(6, producto.getStockMinimo());
            ps.setInt(7, producto.getCategoria().getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al insertar el producto.", e);
        }
    }

    @Override
    public void actualizar(Producto producto) {
        String sql = "UPDATE productos SET codigo = ?, nombre = ?, precio = ?, stock = ?, "
                + "stock_minimo = ?, categoria_id = ? WHERE id = ?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setBigDecimal(3, BigDecimal.valueOf(producto.getPrecio()));
            ps.setInt(4, producto.getStock());
            ps.setInt(5, producto.getStockMinimo());
            ps.setInt(6, producto.getCategoria().getId());
            ps.setInt(7, producto.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al actualizar el producto.", e);
        }
    }

    /** Actualiza unicamente el stock (usado durante ventas y cancelaciones). */
    public void actualizarStock(Producto producto) {
        String sql = "UPDATE productos SET stock = ? WHERE id = ?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, producto.getStock());
            ps.setInt(2, producto.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al actualizar el stock del producto.", e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM productos WHERE id = ?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al eliminar el producto.", e);
        }
    }

    /** Indica si el producto aparece en algun detalle de pedido (integridad referencial). */
    public boolean tieneDetalles(int productoId) {
        String sql = "SELECT 1 FROM detalles WHERE producto_id = ? LIMIT 1";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, productoId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al verificar detalles del producto.", e);
        }
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria(
                rs.getInt("categoria_id"),
                rs.getString("categoria_nombre"));
        return new Producto(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getBigDecimal("precio").doubleValue(),
                rs.getInt("stock"),
                rs.getInt("stock_minimo"),
                categoria);
    }
}
