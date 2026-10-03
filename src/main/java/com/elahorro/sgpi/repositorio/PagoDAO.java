package com.elahorro.sgpi.repositorio;

import com.elahorro.sgpi.config.Conexion;
import com.elahorro.sgpi.modelo.Pago;
import com.elahorro.sgpi.modelo.enums.MetodoPago;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla `pagos` mediante JDBC.
 */
public class PagoDAO implements Repositorio<Pago> {

    @Override
    public List<Pago> listar() {
        String sql = "SELECT id, fecha, monto, metodo FROM pagos ORDER BY id";
        List<Pago> lista = new ArrayList<>();
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al listar pagos.", e);
        }
        return lista;
    }

    @Override
    public void insertar(Pago pago) {
        try (Connection cn = Conexion.getConnection()) {
            insertar(cn, pago);
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al insertar el pago.", e);
        }
    }

    /** Variante que reutiliza una conexion existente (para transacciones). */
    void insertar(Connection cn, Pago pago) throws AccesoDatosException {
        String sql = "INSERT INTO pagos (id, fecha, monto, metodo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, pago.getId());
            ps.setDate(2, Date.valueOf(pago.getFecha()));
            ps.setBigDecimal(3, BigDecimal.valueOf(pago.getMonto()));
            ps.setString(4, pago.getMetodo().name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al insertar el pago.", e);
        }
    }

    @Override
    public void actualizar(Pago pago) {
        String sql = "UPDATE pagos SET fecha = ?, monto = ?, metodo = ? WHERE id = ?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(pago.getFecha()));
            ps.setBigDecimal(2, BigDecimal.valueOf(pago.getMonto()));
            ps.setString(3, pago.getMetodo().name());
            ps.setInt(4, pago.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al actualizar el pago.", e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM pagos WHERE id = ?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al eliminar el pago.", e);
        }
    }

    private Pago mapear(ResultSet rs) throws SQLException {
        return new Pago(
                rs.getInt("id"),
                rs.getDate("fecha").toLocalDate(),
                rs.getBigDecimal("monto").doubleValue(),
                MetodoPago.valueOf(rs.getString("metodo")));
    }
}
