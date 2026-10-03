package com.elahorro.sgpi.repositorio;

import com.elahorro.sgpi.config.Conexion;
import com.elahorro.sgpi.modelo.Usuario;
import com.elahorro.sgpi.modelo.enums.Rol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla `usuarios` mediante JDBC.
 */
public class UsuarioDAO implements Repositorio<Usuario> {

    @Override
    public List<Usuario> listar() {
        String sql = "SELECT id, nombre, username, password_hash, rol FROM usuarios "
                + "ORDER BY username";
        List<Usuario> lista = new ArrayList<>();
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        Rol.valueOf(rs.getString("rol"))));
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al listar usuarios.", e);
        }
        return lista;
    }

    @Override
    public void insertar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (id, nombre, username, password_hash, rol) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, usuario.getId());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getUsername());
            ps.setString(4, usuario.getPasswordHash());
            ps.setString(5, usuario.getRol().name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al insertar el usuario.", e);
        }
    }

    @Override
    public void actualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre = ?, username = ?, password_hash = ?, rol = ? "
                + "WHERE id = ?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getUsername());
            ps.setString(3, usuario.getPasswordHash());
            ps.setString(4, usuario.getRol().name());
            ps.setInt(5, usuario.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al actualizar el usuario.", e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al eliminar el usuario.", e);
        }
    }

    /** Indica si el usuario registro pedidos (integridad referencial). */
    public boolean tienePedidos(int usuarioId) {
        String sql = "SELECT 1 FROM pedidos WHERE vendedor_id = ? LIMIT 1";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al verificar pedidos del usuario.", e);
        }
    }
}
