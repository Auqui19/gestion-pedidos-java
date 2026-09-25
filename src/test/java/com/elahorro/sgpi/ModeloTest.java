package com.elahorro.sgpi;

import com.elahorro.sgpi.modelo.Categoria;
import com.elahorro.sgpi.modelo.Cliente;
import com.elahorro.sgpi.modelo.Pago;
import com.elahorro.sgpi.modelo.Pedido;
import com.elahorro.sgpi.modelo.Producto;
import com.elahorro.sgpi.modelo.Usuario;
import com.elahorro.sgpi.modelo.enums.MetodoPago;
import com.elahorro.sgpi.modelo.enums.Rol;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModeloTest {

    private final Categoria categoria = new Categoria(1, "Celulares");
    private final Usuario vendedor =
            Usuario.conPasswordPlano(1, "Vendedor", "vend", "1234", Rol.VENDEDOR);
    private final Cliente cliente =
            new Cliente(1, "Juan Perez", "12345678", "999888777", "Calle 1");

    @Test
    void pedidoRequiereCliente() {
        assertThrows(IllegalArgumentException.class,
                () -> new Pedido(1, null, vendedor));
    }

    @Test
    void productoConPrecioInvalidoFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> new Producto(1, "P1", "Samsung Galaxy A54", 0, 5, 1, categoria));
    }

    @Test
    void noSeVendeMasStockDelDisponible() {
        Producto producto = new Producto(1, "P1", "Samsung Galaxy A54", 3.5, 5, 1, categoria);
        Pedido pedido = new Pedido(1, cliente, vendedor);
        assertThrows(IllegalStateException.class,
                () -> pedido.agregarDetalle(producto, 10));
    }

    @Test
    void totalSumaSubtotales() {
        Producto celular = new Producto(1, "P1", "Samsung Galaxy A54", 3.5, 10, 1, categoria);
        Producto laptop = new Producto(2, "P2", "Laptop HP 15", 4.2, 10, 1, categoria);
        Pedido pedido = new Pedido(1, cliente, vendedor);
        pedido.agregarDetalle(celular, 2);
        pedido.agregarDetalle(laptop, 1);
        assertEquals(11.20, pedido.calcularTotal(), 0.001);
    }

    @Test
    void descuentoMayorAl20SinAutorizacionFalla() {
        Pedido pedido = new Pedido(1, cliente, vendedor);
        assertThrows(IllegalArgumentException.class,
                () -> pedido.setDescuento(25, false));
    }

    @Test
    void pagoConMontoDistintoFalla() {
        Producto celular = new Producto(1, "P1", "Samsung Galaxy A54", 3.5, 10, 1, categoria);
        Pedido pedido = new Pedido(1, cliente, vendedor);
        pedido.agregarDetalle(celular, 2);
        Pago pago = new Pago(1, LocalDate.now(), 1.0, MetodoPago.EFECTIVO);
        assertThrows(IllegalArgumentException.class, () -> pedido.pagar(pago));
    }

    @Test
    void cancelarReponeStock() {
        Producto celular = new Producto(1, "P1", "Samsung Galaxy A54", 3.5, 10, 1, categoria);
        Pedido pedido = new Pedido(1, cliente, vendedor);
        pedido.agregarDetalle(celular, 3);
        assertEquals(7, celular.getStock());
        pedido.cancelar();
        assertEquals(10, celular.getStock());
    }

    @Test
    void stockBajoSeDetecta() {
        Producto producto = new Producto(1, "P1", "Samsung Galaxy A54", 3.5, 2, 5, categoria);
        assertTrue(producto.esStockBajo());
    }

    @Test
    void passwordSeCifra() {
        Usuario usuario =
                Usuario.conPasswordPlano(1, "Admin", "admin", "admin123", Rol.ADMINISTRADOR);
        assertFalse(usuario.getPasswordHash().equals("admin123"));
        assertTrue(usuario.autenticar("admin123"));
    }
}
