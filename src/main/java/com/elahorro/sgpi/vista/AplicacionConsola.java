package com.elahorro.sgpi.vista;

import com.elahorro.sgpi.modelo.Categoria;
import com.elahorro.sgpi.modelo.Cliente;
import com.elahorro.sgpi.modelo.DetallePedido;
import com.elahorro.sgpi.modelo.Pedido;
import com.elahorro.sgpi.modelo.Producto;
import com.elahorro.sgpi.modelo.Usuario;
import com.elahorro.sgpi.modelo.enums.MetodoPago;
import com.elahorro.sgpi.modelo.enums.Rol;
import com.elahorro.sgpi.servicio.Sistema;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz de usuario por consola del SGPI. Reemplaza a la capa Swing:
 * toda la interaccion se realiza por entrada/salida de texto.
 */
public class AplicacionConsola {

    private final Sistema sistema;
    private final Consola consola = new Consola();
    private Usuario usuarioActual;

    public AplicacionConsola(Sistema sistema) {
        this.sistema = sistema;
    }

    public void iniciar() {
        consola.banner();
        if (!iniciarSesion()) {
            System.out.println("No se inicio sesion. Saliendo del sistema.");
            return;
        }
        menuPrincipal();
    }

    // ------------------------------------------------------------------
    // Sesion
    // ------------------------------------------------------------------

    private boolean iniciarSesion() {
        int intentos = 0;
        while (intentos < 3) {
            System.out.println("\n--- INICIAR SESION ---");
            String username = consola.leerTexto("Usuario: ");
            String password = consola.leerTexto("Contrasena: ");
            Usuario encontrado = sistema.usuarios.login(username, password);
            if (encontrado != null) {
                usuarioActual = encontrado;
                System.out.println("\nBienvenido, " + usuarioActual.getNombre()
                        + " (" + usuarioActual.getRol() + ")");
                return true;
            }
            intentos++;
            System.out.println("Credenciales incorrectas. Intento " + intentos + " de 3.");
        }
        System.out.println("Usuario por defecto: admin / admin123");
        return false;
    }

    private void menuPrincipal() {
        boolean salir = false;
        while (!salir) {
            consola.titulo("MENU PRINCIPAL - " + usuarioActual.getUsername()
                    + " (" + usuarioActual.getRol() + ")");
            System.out.println("1. Productos");
            System.out.println("2. Categorias");
            System.out.println("3. Clientes");
            System.out.println("4. Pedidos");
            System.out.println("5. Reportes");
            if (usuarioActual.esAdministrador()) {
                System.out.println("6. Usuarios");
            }
            System.out.println("0. Cerrar sesion / Salir");
            int opcion = consola.leerEntero("Opcion: ");

            switch (opcion) {
                case 1 -> menuProductos();
                case 2 -> menuCategorias();
                case 3 -> menuClientes();
                case 4 -> menuPedidos();
                case 5 -> menuReportes();
                case 6 -> {
                    if (usuarioActual.esAdministrador()) {
                        menuUsuarios();
                    } else {
                        System.out.println("Opcion no valida.");
                    }
                }
                case 0 -> {
                    if (consola.confirmar("Desea cerrar la sesion y salir?")) {
                        System.out.println("Hasta luego.");
                        salir = true;
                    }
                }
                default -> System.out.println("Opcion no valida.");
            }
        }
    }

    // ------------------------------------------------------------------
    // Productos
    // ------------------------------------------------------------------

    private void menuProductos() {
        boolean volver = false;
        while (!volver) {
            consola.titulo("GESTION DE PRODUCTOS");
            System.out.println("1. Listar productos");
            System.out.println("2. Registrar producto");
            System.out.println("3. Editar producto");
            System.out.println("4. Eliminar producto (solo administrador)");
            System.out.println("5. Buscar producto");
            System.out.println("6. Productos con stock bajo");
            System.out.println("0. Volver");
            int opcion = consola.leerEntero("Opcion: ");
            try {
                switch (opcion) {
                    case 1 -> listarProductos(sistema.productos.listar());
                    case 2 -> registrarProducto();
                    case 3 -> editarProducto();
                    case 4 -> eliminarProducto();
                    case 5 -> buscarProducto();
                    case 6 -> listarProductos(sistema.productos.listarStockBajo());
                    case 0 -> volver = true;
                    default -> System.out.println("Opcion no valida.");
                }
            } catch (RuntimeException e) {
                System.out.println("  Error: " + e.getMessage());
            }
            if (opcion != 0) {
                consola.pausa();
            }
        }
    }

    private void listarProductos(List<Producto> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay productos para mostrar.");
            return;
        }
        System.out.printf("%-4s %-8s %-20s %9s %7s %7s %-12s%n",
                "ID", "CODIGO", "NOMBRE", "PRECIO", "STOCK", "MIN", "CATEGORIA");
        consola.separador();
        for (Producto p : lista) {
            System.out.printf("%-4d %-8s %-20s %9.2f %7d %7d %-12s%s%n",
                    p.getId(), p.getCodigo(), p.getNombre(), p.getPrecio(),
                    p.getStock(), p.getStockMinimo(), p.getCategoria().getNombre(),
                    p.esStockBajo() ? "  <-- STOCK BAJO" : "");
        }
    }

    private void registrarProducto() {
        String codigo = consola.leerTexto("Codigo: ");
        String nombre = consola.leerTexto("Nombre: ");
        double precio = consola.leerDouble("Precio: ");
        int stock = consola.leerEntero("Stock: ");
        int stockMinimo = consola.leerEntero("Stock minimo: ");
        Categoria categoria = elegirCategoria();
        if (categoria == null) {
            return;
        }
        Producto producto = sistema.productos.registrar(
                codigo, nombre, precio, stock, stockMinimo, categoria);
        System.out.println("Producto registrado correctamente: " + producto.getNombre());
    }

    private void editarProducto() {
        Producto producto = elegirProducto();
        if (producto == null) {
            return;
        }
        System.out.println("Deje el valor actual y presione ENTER para no cambiarlo.");
        String codigo = leerConValor("Codigo", producto.getCodigo());
        String nombre = leerConValor("Nombre", producto.getNombre());
        double precio = leerDoubleConValor("Precio", producto.getPrecio());
        int stock = leerEnteroConValor("Stock", producto.getStock());
        int stockMinimo = leerEnteroConValor("Stock minimo", producto.getStockMinimo());
        Categoria categoria = producto.getCategoria();
        if (consola.confirmar("Desea cambiar la categoria?")) {
            Categoria nueva = elegirCategoria();
            if (nueva != null) {
                categoria = nueva;
            }
        }
        sistema.productos.actualizar(producto, codigo, nombre, precio, stock, stockMinimo, categoria);
        System.out.println("Producto actualizado correctamente.");
    }

    private void eliminarProducto() {
        Producto producto = elegirProducto();
        if (producto == null) {
            return;
        }
        if (!consola.confirmar("Eliminar el producto " + producto.getNombre() + "?")) {
            return;
        }
        sistema.productos.eliminar(usuarioActual, producto);
        System.out.println("Producto eliminado.");
    }

    private void buscarProducto() {
        String texto = consola.leerTexto("Nombre o codigo a buscar: ");
        listarProductos(sistema.productos.buscar(texto));
    }

    private Producto elegirProducto() {
        List<Producto> productos = sistema.productos.listar();
        if (productos.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return null;
        }
        for (int i = 0; i < productos.size(); i++) {
            Producto p = productos.get(i);
            System.out.println((i + 1) + ". " + p.getCodigo() + " - " + p.getNombre()
                    + " (stock " + p.getStock() + ")");
        }
        int indice = consola.leerEntero("Seleccione producto: ") - 1;
        if (indice < 0 || indice >= productos.size()) {
            System.out.println("Producto no valido.");
            return null;
        }
        return productos.get(indice);
    }

    // ------------------------------------------------------------------
    // Categorias
    // ------------------------------------------------------------------

    private void menuCategorias() {
        boolean volver = false;
        while (!volver) {
            consola.titulo("GESTION DE CATEGORIAS");
            System.out.println("1. Listar categorias");
            System.out.println("2. Registrar categoria");
            System.out.println("3. Editar categoria");
            System.out.println("4. Eliminar categoria");
            System.out.println("0. Volver");
            int opcion = consola.leerEntero("Opcion: ");
            try {
                switch (opcion) {
                    case 1 -> listarCategorias();
                    case 2 -> registrarCategoria();
                    case 3 -> editarCategoria();
                    case 4 -> eliminarCategoria();
                    case 0 -> volver = true;
                    default -> System.out.println("Opcion no valida.");
                }
            } catch (RuntimeException e) {
                System.out.println("  Error: " + e.getMessage());
            }
            if (opcion != 0) {
                consola.pausa();
            }
        }
    }

    private void listarCategorias() {
        List<Categoria> categorias = sistema.categorias.listar();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorias registradas.");
            return;
        }
        for (Categoria c : categorias) {
            System.out.println(c.getId() + ". " + c.getNombre());
        }
    }

    private void registrarCategoria() {
        String nombre = consola.leerTexto("Nombre de la categoria: ");
        Categoria categoria = sistema.categorias.registrar(nombre);
        System.out.println("Categoria registrada: " + categoria.getNombre());
    }

    private void editarCategoria() {
        Categoria categoria = elegirCategoria();
        if (categoria == null) {
            return;
        }
        String nombre = leerConValor("Nuevo nombre", categoria.getNombre());
        sistema.categorias.actualizar(categoria, nombre);
        System.out.println("Categoria actualizada.");
    }

    private void eliminarCategoria() {
        Categoria categoria = elegirCategoria();
        if (categoria == null) {
            return;
        }
        if (!consola.confirmar("Eliminar la categoria " + categoria.getNombre() + "?")) {
            return;
        }
        sistema.categorias.eliminar(categoria);
        System.out.println("Categoria eliminada.");
    }

    private Categoria elegirCategoria() {
        List<Categoria> categorias = sistema.categorias.listar();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorias. Registre una primero.");
            return null;
        }
        for (int i = 0; i < categorias.size(); i++) {
            System.out.println((i + 1) + ". " + categorias.get(i).getNombre());
        }
        int indice = consola.leerEntero("Seleccione categoria: ") - 1;
        if (indice < 0 || indice >= categorias.size()) {
            System.out.println("Categoria no valida.");
            return null;
        }
        return categorias.get(indice);
    }

    // ------------------------------------------------------------------
    // Clientes
    // ------------------------------------------------------------------

    private void menuClientes() {
        boolean volver = false;
        while (!volver) {
            consola.titulo("GESTION DE CLIENTES");
            System.out.println("1. Listar clientes");
            System.out.println("2. Registrar cliente");
            System.out.println("3. Editar cliente");
            System.out.println("4. Eliminar cliente");
            System.out.println("5. Buscar cliente");
            System.out.println("0. Volver");
            int opcion = consola.leerEntero("Opcion: ");
            try {
                switch (opcion) {
                    case 1 -> listarClientes(sistema.clientes.listar());
                    case 2 -> registrarCliente();
                    case 3 -> editarCliente();
                    case 4 -> eliminarCliente();
                    case 5 -> buscarCliente();
                    case 0 -> volver = true;
                    default -> System.out.println("Opcion no valida.");
                }
            } catch (RuntimeException e) {
                System.out.println("  Error: " + e.getMessage());
            }
            if (opcion != 0) {
                consola.pausa();
            }
        }
    }

    private void listarClientes(List<Cliente> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay clientes para mostrar.");
            return;
        }
        System.out.printf("%-4s %-20s %-10s %-12s %-20s%n",
                "ID", "NOMBRE", "DNI", "TELEFONO", "DIRECCION");
        consola.separador();
        for (Cliente c : lista) {
            System.out.printf("%-4d %-20s %-10s %-12s %-20s%n",
                    c.getId(), c.getNombre(), c.getDni(), c.getTelefono(), c.getDireccion());
        }
    }

    private void registrarCliente() {
        String nombre = consola.leerTexto("Nombre: ");
        String dni = consola.leerTexto("DNI (8 digitos): ");
        String telefono = consola.leerTexto("Telefono: ");
        String direccion = consola.leerTexto("Direccion: ");
        Cliente cliente = sistema.clientes.registrar(nombre, dni, telefono, direccion);
        System.out.println("Cliente registrado: " + cliente.getNombre());
    }

    private void editarCliente() {
        Cliente cliente = elegirCliente();
        if (cliente == null) {
            return;
        }
        System.out.println("Deje el valor actual y presione ENTER para no cambiarlo.");
        String nombre = leerConValor("Nombre", cliente.getNombre());
        String dni = leerConValor("DNI", cliente.getDni());
        String telefono = leerConValor("Telefono", cliente.getTelefono());
        String direccion = leerConValor("Direccion", cliente.getDireccion());
        sistema.clientes.actualizar(cliente, nombre, dni, telefono, direccion);
        System.out.println("Cliente actualizado.");
    }

    private void eliminarCliente() {
        Cliente cliente = elegirCliente();
        if (cliente == null) {
            return;
        }
        if (!consola.confirmar("Eliminar el cliente " + cliente.getNombre() + "?")) {
            return;
        }
        sistema.clientes.eliminar(cliente);
        System.out.println("Cliente eliminado.");
    }

    private void buscarCliente() {
        String texto = consola.leerTexto("Nombre o DNI a buscar: ");
        Cliente porDni = sistema.clientes.buscarPorDni(texto);
        if (porDni != null) {
            listarClientes(List.of(porDni));
            return;
        }
        listarClientes(sistema.clientes.buscarPorNombre(texto));
    }

    private Cliente elegirCliente() {
        List<Cliente> clientes = sistema.clientes.listar();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return null;
        }
        for (int i = 0; i < clientes.size(); i++) {
            Cliente c = clientes.get(i);
            System.out.println((i + 1) + ". " + c.getNombre() + " (DNI " + c.getDni() + ")");
        }
        int indice = consola.leerEntero("Seleccione cliente: ") - 1;
        if (indice < 0 || indice >= clientes.size()) {
            System.out.println("Cliente no valido.");
            return null;
        }
        return clientes.get(indice);
    }

    // ------------------------------------------------------------------
    // Pedidos
    // ------------------------------------------------------------------

    private void menuPedidos() {
        boolean volver = false;
        while (!volver) {
            consola.titulo("GESTION DE PEDIDOS");
            System.out.println("1. Listar pedidos");
            System.out.println("2. Nuevo pedido");
            System.out.println("3. Agregar producto a un pedido");
            System.out.println("4. Aplicar descuento");
            System.out.println("5. Registrar pago");
            System.out.println("6. Confirmar pedido");
            System.out.println("7. Cancelar pedido");
            System.out.println("8. Ver detalle de un pedido");
            System.out.println("0. Volver");
            int opcion = consola.leerEntero("Opcion: ");
            try {
                switch (opcion) {
                    case 1 -> listarPedidos();
                    case 2 -> nuevoPedido();
                    case 3 -> agregarProducto();
                    case 4 -> aplicarDescuento();
                    case 5 -> registrarPago();
                    case 6 -> confirmarPedido();
                    case 7 -> cancelarPedido();
                    case 8 -> verDetalle();
                    case 0 -> volver = true;
                    default -> System.out.println("Opcion no valida.");
                }
            } catch (RuntimeException e) {
                System.out.println("  Error: " + e.getMessage());
            }
            if (opcion != 0) {
                consola.pausa();
            }
        }
    }

    private void listarPedidos() {
        List<Pedido> pedidos = sistema.pedidos.listar();
        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos registrados.");
            return;
        }
        System.out.printf("%-4s %-12s %-18s %-12s %-12s %10s%n",
                "ID", "FECHA", "CLIENTE", "VENDEDOR", "ESTADO", "TOTAL");
        consola.separador();
        for (Pedido p : pedidos) {
            System.out.printf("%-4d %-12s %-18s %-12s %-12s %10.2f%n",
                    p.getId(), p.getFecha(), p.getCliente().getNombre(),
                    p.getVendedor().getUsername(), p.getEstado(), p.getTotal());
        }
    }

    private void nuevoPedido() {
        Cliente cliente = elegirCliente();
        if (cliente == null) {
            return;
        }
        Pedido pedido = sistema.pedidos.crear(cliente, usuarioActual);
        System.out.println("Pedido #" + pedido.getId() + " creado en estado PENDIENTE.");
    }

    private void agregarProducto() {
        Pedido pedido = elegirPedido();
        if (pedido == null) {
            return;
        }
        Producto producto = elegirProducto();
        if (producto == null) {
            return;
        }
        int cantidad = consola.leerEntero("Cantidad: ");
        sistema.pedidos.agregarProducto(pedido, producto, cantidad);
        System.out.printf("Producto agregado. Total del pedido: S/ %.2f%n", pedido.getTotal());
    }

    private void aplicarDescuento() {
        Pedido pedido = elegirPedido();
        if (pedido == null) {
            return;
        }
        double descuento = consola.leerDouble("Descuento (%) maximo 20 sin autorizacion: ");
        boolean autorizado = usuarioActual.esAdministrador();
        if (descuento > Pedido.DESCUENTO_MAX_SIN_AUTORIZACION && !autorizado) {
            System.out.println("Aviso: como no es administrador, solo se permite hasta 20%.");
        }
        sistema.pedidos.aplicarDescuento(pedido, descuento, autorizado);
        System.out.printf("Descuento aplicado. Total: S/ %.2f%n", pedido.getTotal());
    }

    private void registrarPago() {
        Pedido pedido = elegirPedido();
        if (pedido == null) {
            return;
        }
        MetodoPago metodo = elegirMetodoPago();
        sistema.pedidos.registrarPago(pedido, metodo);
        System.out.println("Pago registrado. El pedido quedo PAGADO.");
    }

    private void confirmarPedido() {
        Pedido pedido = elegirPedido();
        if (pedido == null) {
            return;
        }
        sistema.pedidos.confirmar(pedido);
        System.out.println("Pedido confirmado.");
    }

    private void cancelarPedido() {
        Pedido pedido = elegirPedido();
        if (pedido == null) {
            return;
        }
        if (!consola.confirmar("Cancelar el pedido #" + pedido.getId()
                + "? Se repondra el stock")) {
            return;
        }
        sistema.pedidos.cancelar(pedido);
        System.out.println("Pedido cancelado y stock repuesto.");
    }

    private void verDetalle() {
        Pedido pedido = elegirPedido();
        if (pedido == null) {
            return;
        }
        consola.separador();
        System.out.println("Pedido #" + pedido.getId());
        System.out.println("Cliente: " + pedido.getCliente().getNombre()
                + " (DNI " + pedido.getCliente().getDni() + ")");
        System.out.println("Vendedor: " + pedido.getVendedor().getUsername());
        System.out.println("Fecha: " + pedido.getFecha() + "  Estado: " + pedido.getEstado());
        consola.separador();
        for (DetallePedido d : pedido.getDetalles()) {
            System.out.printf("  %s x%d = S/ %.2f%n",
                    d.getProducto().getNombre(), d.getCantidad(), d.getSubtotal());
        }
        consola.separador();
        System.out.printf("Descuento: %.2f%%%n", pedido.getDescuento());
        System.out.printf("TOTAL: S/ %.2f%n", pedido.getTotal());
        if (pedido.getPago() != null) {
            System.out.println("Pago: " + pedido.getPago());
        }
    }

    private Pedido elegirPedido() {
        List<Pedido> pedidos = sistema.pedidos.listar();
        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos registrados.");
            return null;
        }
        for (int i = 0; i < pedidos.size(); i++) {
            Pedido p = pedidos.get(i);
            System.out.println((i + 1) + ". #" + p.getId() + " " + p.getCliente().getNombre()
                    + " [" + p.getEstado() + "] S/ " + String.format("%.2f", p.getTotal()));
        }
        int indice = consola.leerEntero("Seleccione pedido: ") - 1;
        if (indice < 0 || indice >= pedidos.size()) {
            System.out.println("Pedido no valido.");
            return null;
        }
        return pedidos.get(indice);
    }

    private MetodoPago elegirMetodoPago() {
        MetodoPago[] metodos = MetodoPago.values();
        for (int i = 0; i < metodos.length; i++) {
            System.out.println((i + 1) + ". " + metodos[i]);
        }
        int indice = consola.leerEntero("Seleccione metodo de pago: ") - 1;
        if (indice < 0 || indice >= metodos.length) {
            System.out.println("Metodo no valido. Se usara EFECTIVO.");
            return MetodoPago.EFECTIVO;
        }
        return metodos[indice];
    }

    // ------------------------------------------------------------------
    // Reportes
    // ------------------------------------------------------------------

    private void menuReportes() {
        boolean volver = false;
        while (!volver) {
            consola.titulo("REPORTES");
            System.out.println("1. Reporte de stock bajo");
            System.out.println("2. Reporte de ventas por fecha");
            System.out.println("3. Exportar stock bajo a CSV");
            System.out.println("4. Exportar ventas a CSV");
            System.out.println("0. Volver");
            int opcion = consola.leerEntero("Opcion: ");
            try {
                switch (opcion) {
                    case 1 -> reporteStockBajo();
                    case 2 -> reporteVentas();
                    case 3 -> exportarStockBajo();
                    case 4 -> exportarVentas();
                    case 0 -> volver = true;
                    default -> System.out.println("Opcion no valida.");
                }
            } catch (RuntimeException e) {
                System.out.println("  Error: " + e.getMessage());
            }
            if (opcion != 0) {
                consola.pausa();
            }
        }
    }

    private void reporteStockBajo() {
        List<Producto> lista = sistema.reportes.stockBajo();
        if (lista.isEmpty()) {
            System.out.println("No hay productos con stock bajo.");
            return;
        }
        System.out.printf("%-8s %-20s %7s %7s %-12s%n",
                "CODIGO", "NOMBRE", "STOCK", "MIN", "CATEGORIA");
        consola.separador();
        for (Producto p : lista) {
            System.out.printf("%-8s %-20s %7d %7d %-12s%n",
                    p.getCodigo(), p.getNombre(), p.getStock(), p.getStockMinimo(),
                    p.getCategoria().getNombre());
        }
    }

    private void reporteVentas() {
        LocalDate desde = leerFecha("Desde (AAAA-MM-DD): ");
        LocalDate hasta = leerFecha("Hasta (AAAA-MM-DD): ");
        List<Pedido> ventas = sistema.reportes.ventasPorFecha(desde, hasta);
        if (ventas.isEmpty()) {
            System.out.println("No hay ventas en ese rango de fechas.");
            return;
        }
        System.out.printf("%-4s %-12s %-18s %-12s %10s%n",
                "ID", "FECHA", "CLIENTE", "VENDEDOR", "TOTAL");
        consola.separador();
        for (Pedido p : ventas) {
            System.out.printf("%-4d %-12s %-18s %-12s %10.2f%n",
                    p.getId(), p.getFecha(), p.getCliente().getNombre(),
                    p.getVendedor().getUsername(), p.getTotal());
        }
        consola.separador();
        System.out.printf("TOTAL VENDIDO (%s a %s): S/ %.2f%n",
                desde, hasta, sistema.reportes.totalVentas(desde, hasta));
    }

    private void exportarStockBajo() {
        sistema.reportes.exportarStockBajo("datos/reporte_stock_bajo.csv");
        System.out.println("Reporte exportado a datos/reporte_stock_bajo.csv");
    }

    private void exportarVentas() {
        LocalDate desde = leerFecha("Desde (AAAA-MM-DD): ");
        LocalDate hasta = leerFecha("Hasta (AAAA-MM-DD): ");
        sistema.reportes.exportarVentas("datos/reporte_ventas.csv", desde, hasta);
        System.out.println("Reporte exportado a datos/reporte_ventas.csv");
    }

    // ------------------------------------------------------------------
    // Usuarios (solo administrador)
    // ------------------------------------------------------------------

    private void menuUsuarios() {
        boolean volver = false;
        while (!volver) {
            consola.titulo("GESTION DE USUARIOS (ADMINISTRADOR)");
            System.out.println("1. Listar usuarios");
            System.out.println("2. Registrar usuario");
            System.out.println("3. Eliminar usuario");
            System.out.println("0. Volver");
            int opcion = consola.leerEntero("Opcion: ");
            try {
                switch (opcion) {
                    case 1 -> listarUsuarios();
                    case 2 -> registrarUsuario();
                    case 3 -> eliminarUsuario();
                    case 0 -> volver = true;
                    default -> System.out.println("Opcion no valida.");
                }
            } catch (RuntimeException e) {
                System.out.println("  Error: " + e.getMessage());
            }
            if (opcion != 0) {
                consola.pausa();
            }
        }
    }

    private void listarUsuarios() {
        List<Usuario> usuarios = sistema.usuarios.listar();
        System.out.printf("%-4s %-20s %-14s %-14s%n", "ID", "NOMBRE", "USUARIO", "ROL");
        consola.separador();
        for (Usuario u : usuarios) {
            System.out.printf("%-4d %-20s %-14s %-14s%n",
                    u.getId(), u.getNombre(), u.getUsername(), u.getRol());
        }
    }

    private void registrarUsuario() {
        String nombre = consola.leerTexto("Nombre: ");
        String username = consola.leerTexto("Usuario: ");
        String password = consola.leerTexto("Contrasena: ");
        Rol rol = elegirRol();
        sistema.usuarios.registrar(usuarioActual, nombre, username, password, rol);
        System.out.println("Usuario registrado correctamente.");
    }

    private void eliminarUsuario() {
        listarUsuarios();
        int id = consola.leerEntero("ID del usuario a eliminar: ");
        if (!consola.confirmar("Confirma eliminar el usuario con ID " + id + "?")) {
            return;
        }
        sistema.usuarios.eliminar(usuarioActual, id);
        System.out.println("Usuario eliminado.");
    }

    private Rol elegirRol() {
        Rol[] roles = Rol.values();
        for (int i = 0; i < roles.length; i++) {
            System.out.println((i + 1) + ". " + roles[i]);
        }
        int indice = consola.leerEntero("Seleccione rol: ") - 1;
        if (indice < 0 || indice >= roles.length) {
            System.out.println("Rol no valido. Se usara VENDEDOR.");
            return Rol.VENDEDOR;
        }
        return roles[indice];
    }

    // ------------------------------------------------------------------
    // Helpers de lectura con valor por defecto
    // ------------------------------------------------------------------

    private String leerConValor(String etiqueta, String actual) {
        String valor = consola.leerTexto(etiqueta + " [" + actual + "]: ");
        return valor.isEmpty() ? actual : valor;
    }

    private int leerEnteroConValor(String etiqueta, int actual) {
        String valor = consola.leerTexto(etiqueta + " [" + actual + "]: ");
        if (valor.isEmpty()) {
            return actual;
        }
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            System.out.println("  Valor no valido, se conserva " + actual + ".");
            return actual;
        }
    }

    private double leerDoubleConValor(String etiqueta, double actual) {
        String valor = consola.leerTexto(etiqueta + " [" + actual + "]: ");
        if (valor.isEmpty()) {
            return actual;
        }
        try {
            return Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            System.out.println("  Valor no valido, se conserva " + actual + ".");
            return actual;
        }
    }

    private LocalDate leerFecha(String etiqueta) {
        while (true) {
            String valor = consola.leerTexto(etiqueta);
            try {
                return LocalDate.parse(valor);
            } catch (RuntimeException e) {
                System.out.println("  Error: use el formato AAAA-MM-DD.");
            }
        }
    }
}
