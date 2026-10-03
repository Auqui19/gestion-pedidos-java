package com.elahorro.sgpi.repositorio;

import com.elahorro.sgpi.config.Conexion;
import com.elahorro.sgpi.modelo.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla `clientes` mediante JDBC.
 */
public class ClienteDAO implements Repositorio<Cliente> {

    @Override
    public List<Cliente> listar() {
        String sql = "SELECT id, nombre, dni, telefono, direccion FROM clientes ORDER BY nombre";
        List<Cliente> lista = new ArrayList<>();
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Cliente(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("dni"),
                        rs.getString("telefono"),
                        rs.getString("direccion")));
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al listar clientes.", e);
        }
        return lista;
    }

    @Override
    public void insertar(Cliente cliente) {
        String sql = "INSERT INTO clientes (id, nombre, dni, telefono, direccion) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, cliente.getId());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getDni());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getDireccion());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al insertar el cliente.", e);
        }
    }

    @Override
    public void actualizar(Cliente cliente) {
        String sql = "UPDATE clientes SET nombre = ?, dni = ?, telefono = ?, direccion = ? "
                + "WHERE id = ?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getDni());
            ps.setString(3, cliente.getTelefono());
            ps.setString(4, cliente.getDireccion());
            ps.setInt(5, cliente.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al actualizar el cliente.", e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al eliminar el cliente.", e);
        }
    }

    /** Indica si el cliente tiene pedidos registrados (integridad referencial). */
    public boolean tienePedidos(int clienteId) {
        String sql = "SELECT 1 FROM pedidos WHERE cliente_id = ? LIMIT 1";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al verificar pedidos del cliente.", e);
        }
    }
}
