package com.elahorro.sgpi.repositorio;

import com.elahorro.sgpi.config.Conexion;
import com.elahorro.sgpi.modelo.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla `categorias` mediante JDBC.
 */
public class CategoriaDAO implements Repositorio<Categoria> {

    @Override
    public List<Categoria> listar() {
        String sql = "SELECT id, nombre FROM categorias ORDER BY nombre";
        List<Categoria> lista = new ArrayList<>();
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Categoria(rs.getInt("id"), rs.getString("nombre")));
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al listar categorias.", e);
        }
        return lista;
    }

    @Override
    public void insertar(Categoria categoria) {
        String sql = "INSERT INTO categorias (id, nombre) VALUES (?, ?)";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, categoria.getId());
            ps.setString(2, categoria.getNombre());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al insertar la categoria.", e);
        }
    }

    @Override
    public void actualizar(Categoria categoria) {
        String sql = "UPDATE categorias SET nombre = ? WHERE id = ?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, categoria.getNombre());
            ps.setInt(2, categoria.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al actualizar la categoria.", e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM categorias WHERE id = ?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al eliminar la categoria.", e);
        }
    }

    /** Indica si la categoria tiene productos asociados (integridad referencial). */
    public boolean tieneProductos(int categoriaId) {
        String sql = "SELECT 1 FROM productos WHERE categoria_id = ? LIMIT 1";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, categoriaId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al verificar productos de la categoria.", e);
        }
    }
}
