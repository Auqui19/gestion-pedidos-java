package com.elahorro.sgpi.pruebas;

import com.elahorro.sgpi.modelo.Cliente;
import com.elahorro.sgpi.modelo.Pago;
import com.elahorro.sgpi.modelo.Pedido;
import com.elahorro.sgpi.modelo.Producto;
import com.elahorro.sgpi.modelo.Usuario;
import com.elahorro.sgpi.modelo.enums.MetodoPago;
import com.elahorro.sgpi.modelo.enums.Rol;
import com.elahorro.sgpi.servicio.Sistema;

import java.io.File;
import java.time.LocalDate;

/**
 * Runner de evidencia: demuestra el funcionamiento de los 20 requerimientos
 * prioritarios del SGPI sin necesidad de JUnit. Ejecutar con:
 *   java -cp target/classes com.elahorro.sgpi.pruebas.PruebasSistema
 *
 * ADVERTENCIA: reinicia la carpeta "datos" para que las pruebas sean
 * repetibles.
 */
public class PruebasSistema {

    private static int ok = 0;
    private static int fallos = 0;

    public static void main(String[] args) {
        reiniciarDatos();
        Sistema s = new Sistema();

        Usuario admin = s.usuarios.login("admin", "admin123");
        Usuario vendedor = s.usuarios.registrar(
                admin, "Vendedor Uno", "vendedor1", "vend123", Rol.VENDEDOR);
        var categoria = s.categorias.listar().get(0);
        Producto celular = s.productos.registrar(
                "T001", "Samsung Galaxy A54", 1299.90, 10, 3, categoria);
        Producto laptop = s.productos.registrar(
                "T002", "Laptop HP 15", 2499.00, 2, 5, categoria);
        Cliente cliente = s.clientes.registrar(
                "Juan Perez", "12345678", "999888777", "Av. Siempre Viva 123");

        prueba("RF-01", "Login correcto", () ->
                espera(s.usuarios.login("admin", "admin123") != null,
                        "El login valido debe funcionar"));
        prueba("RF-01", "Login incorrecto rechazado", () ->
                espera(s.usuarios.login("admin", "mala") == null,
                        "El login invalido debe fallar"));
        prueba("RF-02", "Solo admin elimina usuarios", () ->
                esperaError(() -> s.usuarios.eliminar(vendedor, admin.getId()),
                        "El vendedor no debe poder eliminar usuarios"));
        prueba("RF-03", "Password cifrada (no texto plano)", () ->
                espera(!admin.getPasswordHash().equals("admin123"),
                        "La contrasena debe estar cifrada con SHA-256"));
        prueba("RF-04", "CRUD productos (registrar y buscar)", () ->
                espera(s.productos.buscarPorCodigo("T001") != null,
                        "El producto registrado debe existir"));
        prueba("RF-05", "Busqueda de productos", () ->
                espera(!s.productos.buscar("samsung").isEmpty(),
                        "La busqueda por nombre debe encontrar coincidencias"));
        prueba("RF-06", "Alerta de stock minimo", () ->
                espera(laptop.esStockBajo(),
                        "La laptop (stock 2 <= minimo 5) debe estar en alerta"));
        prueba("RF-07", "Gestion de categorias", () -> {
            int antes = s.categorias.listar().size();
            s.categorias.registrar("Tablets");
            espera(s.categorias.listar().size() == antes + 1,
                    "Debe agregarse la categoria");
        });
        prueba("RF-08", "DNI unico por cliente", () ->
                esperaError(() -> s.clientes.registrar(
                                "Otro", "12345678", "111", "Calle 1"),
                        "No debe permitirse un DNI duplicado"));
        prueba("RF-09", "Busqueda de cliente por nombre", () ->
                espera(!s.clientes.buscarPorNombre("Juan").isEmpty(),
                        "Debe encontrar al cliente Juan"));
        prueba("RF-10", "Pedido requiere cliente (RN-03)", () ->
                esperaError(() -> new Pedido(999, null, vendedor),
                        "No debe crearse un pedido sin cliente"));

        Pedido pedido = s.pedidos.crear(cliente, vendedor);

        prueba("RF-11", "Pedido con multiples detalles", () -> {
            s.pedidos.agregarProducto(pedido, celular, 2);
            s.pedidos.agregarProducto(pedido, laptop, 1);
            espera(pedido.getDetalles().size() == 2,
                    "El pedido debe tener 2 detalles");
        });
        prueba("RF-12", "Validacion de stock (RN-02)", () ->
                esperaError(() -> s.pedidos.agregarProducto(pedido, celular, 9999),
                        "No debe venderse mas stock del disponible"));
        prueba("RF-13", "Calculo de subtotales y total", () ->
                espera(Math.abs(pedido.getTotal() - (2 * 1299.90 + 1 * 2499.00)) < 0.01,
                        "Total = 2*1299.90 + 1*2499.00 = 5098.80"));
        prueba("RF-14", "Descuento maximo 20% sin autorizacion", () ->
                esperaError(() -> pedido.setDescuento(30, false),
                        "Descuento > 20% sin autorizacion debe fallar"));
        prueba("RF-15", "Pago con monto igual al total", () -> {
            esperaError(() -> pedido.pagar(new Pago(
                            1, LocalDate.now(), 1.00, MetodoPago.EFECTIVO)),
                    "Pago con monto distinto debe fallar");
            s.pedidos.registrarPago(pedido, MetodoPago.EFECTIVO);
            espera(pedido.getPago() != null, "El pedido debe quedar pagado");
        });
        prueba("RF-17", "Descuento automatico de stock al vender", () ->
                espera(celular.getStock() == 8,
                        "Stock inicial 10 - 2 = 8"));

        Producto audifonos = s.productos.registrar(
                "T003", "Audifonos JBL", 199.90, 5, 2, categoria);
        Pedido pedidoCancelar = s.pedidos.crear(cliente, vendedor);
        s.pedidos.agregarProducto(pedidoCancelar, audifonos, 3);

        prueba("RF-16", "Cancelacion de pedido repone stock", () -> {
            s.pedidos.cancelar(pedidoCancelar);
            espera(audifonos.getStock() == 5, "El stock debe volver a 5");
        });
        prueba("RF-18", "Reporte de stock bajo", () ->
                espera(!s.reportes.stockBajo().isEmpty(),
                        "Debe listar productos bajo el minimo"));
        prueba("RF-19", "Reporte de ventas por fecha", () ->
                espera(!s.reportes.ventasPorFecha(LocalDate.now(), LocalDate.now()).isEmpty(),
                        "Debe incluir el pedido pagado de hoy"));
        prueba("RF-20", "Exportacion a CSV", () -> {
            s.reportes.exportarStockBajo("datos/prueba_stock_bajo.csv");
            s.reportes.exportarVentas("datos/prueba_ventas.csv",
                    LocalDate.now(), LocalDate.now());
            espera(new File("datos/prueba_stock_bajo.csv").exists(),
                    "Debe generarse el archivo CSV");
        });

        System.out.println("\n========================================");
        System.out.println("RESULTADO: " + ok + " correctas, " + fallos + " fallidas.");
        System.out.println("========================================");
        if (fallos > 0) {
            System.exit(1);
        }
    }

    private static void prueba(String codigo, String descripcion, Runnable cuerpo) {
        try {
            cuerpo.run();
            System.out.println("[OK]    " + codigo + " - " + descripcion);
            ok++;
        } catch (Throwable t) {
            System.out.println("[FALLA] " + codigo + " - " + descripcion
                    + "  (" + t.getMessage() + ")");
            fallos++;
        }
    }

    private static void espera(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }

    private static void esperaError(Runnable accion, String mensaje) {
        try {
            accion.run();
        } catch (RuntimeException e) {
            return;
        }
        throw new AssertionError(mensaje);
    }

    private static void reiniciarDatos() {
        File datos = new File("datos");
        borrarRecursivo(datos);
    }

    private static void borrarRecursivo(File archivo) {
        if (archivo == null || !archivo.exists()) {
            return;
        }
        if (archivo.isDirectory()) {
            File[] hijos = archivo.listFiles();
            if (hijos != null) {
                for (int i = 0; i < hijos.length; i++) {
                    borrarRecursivo(hijos[i]);
                }
            }
        }
        archivo.delete();
    }
}
