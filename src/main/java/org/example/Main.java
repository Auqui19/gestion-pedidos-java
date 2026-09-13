package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final List<Producto> productos = new ArrayList<>();
    private static final List<Cliente> clientes = new ArrayList<>();
    private static final List<Pedido> pedidos = new ArrayList<>();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int opcion;

        do {
            mostrarMenu();
            opcion = leerEntero();

            switch (opcion) {
                case 1 -> registrarProducto();
                case 2 -> registrarCliente();
                case 3 -> registrarPedido();
                case 4 -> consultarProductos();
                case 5 -> consultarClientes();
                case 6 -> consultarPedidos();
                case 7 -> System.out.println("\nHasta luego.");
                default -> System.out.println(
                        "Error: Opcion no valida. Intente nuevamente."
                );
            }

        } while (opcion != 7);
    }

    private static void mostrarMenu() {
        System.out.println("\n=== SISTEMA DE GESTION DE PEDIDOS ===");
        System.out.println("1. Registrar producto");
        System.out.println("2. Registrar cliente");
        System.out.println("3. Registrar pedido");
        System.out.println("4. Consultar productos");
        System.out.println("5. Consultar clientes");
        System.out.println("6. Consultar pedidos");
        System.out.println("7. Salir");
        System.out.print("Seleccione una opcion: ");
    }

    private static void registrarProducto() {
        System.out.println("\n=== REGISTRO DE PRODUCTO ===");

        System.out.print("Ingrese nombre del producto: ");
        String nombre = sc.nextLine();

        /*
         * Se valida primero el nombre para evitar solicitar
         * el precio cuando el nombre no es valido.
         */
        if (nombre == null || nombre.trim().isEmpty()) {
            System.out.println(
                    "Error: El nombre del producto no puede estar vacio."
            );
            return;
        }

        nombre = nombre.trim();

        if (nombre.length() < 3) {
            System.out.println(
                    "Error: El nombre del producto debe tener "
                    + "al menos 3 caracteres."
            );
            return;
        }

        if (productoDuplicado(nombre)) {
            System.out.println(
                    "Error: Ya existe un producto registrado "
                    + "con ese nombre."
            );
            return;
        }

        System.out.print("Ingrese precio del producto: ");
        double precio = leerDouble();

        try {
            Producto producto = new Producto(nombre, precio);
            productos.add(producto);

            System.out.println("\nProducto registrado correctamente.");
            producto.mostrarDatos();

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static boolean productoDuplicado(String nombre) {
        String nombreNormalizado = nombre.trim();

        for (Producto producto : productos) {
            if (producto.getNombre().equalsIgnoreCase(nombreNormalizado)) {
                return true;
            }
        }

        return false;
    }

    private static void registrarCliente() {
        System.out.println("\n=== REGISTRO DE CLIENTE ===");

        System.out.print("Ingrese nombre: ");
        String nombre = sc.nextLine();

        if (nombre == null || nombre.trim().isEmpty()) {
            System.out.println(
                    "Error: El nombre del cliente no puede estar vacio."
            );
            return;
        }

        nombre = nombre.trim();

        System.out.print("Ingrese DNI: ");
        String dni = sc.nextLine().trim();

        if (!dni.matches("\\d{8}")) {
            System.out.println(
                    "Error: El DNI debe contener exactamente 8 numeros."
            );
            return;
        }

        if (clienteDuplicado(dni)) {
            System.out.println(
                    "Error: Ya existe un cliente registrado con ese DNI."
            );
            return;
        }

        Cliente cliente = new Cliente(nombre, dni);
        clientes.add(cliente);

        System.out.println("\nCliente registrado correctamente.");
        cliente.mostrarDatos();
    }

    private static boolean clienteDuplicado(String dni) {
        for (Cliente cliente : clientes) {
            if (cliente.getDni().equals(dni)) {
                return true;
            }
        }

        return false;
    }

    private static void registrarPedido() {
        if (productos.isEmpty()) {
            System.out.println(
                    "Error: No hay productos registrados. "
                    + "Registre un producto primero."
            );
            return;
        }

        if (clientes.isEmpty()) {
            System.out.println(
                    "Error: No hay clientes registrados. "
                    + "Registre un cliente primero."
            );
            return;
        }

        System.out.println("\n=== REGISTRO DE PEDIDO ===");

        consultarClientes();

        System.out.print("Seleccione el numero del cliente: ");
        int indiceCliente = leerEntero() - 1;

        if (indiceCliente < 0 || indiceCliente >= clientes.size()) {
            System.out.println("Error: Cliente no valido.");
            return;
        }

        Cliente cliente = clientes.get(indiceCliente);

        consultarProductos();

        System.out.print("Seleccione el numero del producto: ");
        int indiceProducto = leerEntero() - 1;

        if (indiceProducto < 0 || indiceProducto >= productos.size()) {
            System.out.println("Error: Producto no valido.");
            return;
        }

        Producto producto = productos.get(indiceProducto);

        System.out.print("Ingrese cantidad: ");
        int cantidad = leerEntero();

        try {
            Pedido pedido = new Pedido(producto, cantidad);
            pedidos.add(pedido);

            System.out.println("\nPedido registrado correctamente.");
            System.out.println(
                    "Cliente: "
                    + cliente.getNombre()
                    + " (DNI: "
                    + cliente.getDni()
                    + ")"
            );

            pedido.mostrarPedido();

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void consultarProductos() {
        System.out.println("\n=== LISTA DE PRODUCTOS ===");

        if (productos.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }

        for (int i = 0; i < productos.size(); i++) {
            Producto producto = productos.get(i);

            System.out.printf(
                    "%d. %s - S/ %.2f%n",
                    i + 1,
                    producto.getNombre(),
                    producto.getPrecio()
            );
        }
    }

    private static void consultarClientes() {
        System.out.println("\n=== LISTA DE CLIENTES ===");

        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }

        for (int i = 0; i < clientes.size(); i++) {
            Cliente cliente = clientes.get(i);

            System.out.printf(
                    "%d. %s (DNI: %s)%n",
                    i + 1,
                    cliente.getNombre(),
                    cliente.getDni()
            );
        }
    }

    private static void consultarPedidos() {
        System.out.println("\n=== LISTA DE PEDIDOS ===");

        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos registrados.");
            return;
        }

        double totalGeneral = 0;

        for (int i = 0; i < pedidos.size(); i++) {
            Pedido pedido = pedidos.get(i);

            System.out.println("\nPedido " + (i + 1) + ":");
            pedido.mostrarPedido();
            System.out.println("----------------------------------");

            totalGeneral += pedido.calcularTotal();
        }

        System.out.printf(
                "Importe total de todos los pedidos: S/ %.2f%n",
                totalGeneral
        );
    }

    private static int leerEntero() {
        while (true) {
            String entrada = sc.nextLine().trim();

            try {
                return Integer.parseInt(entrada);

            } catch (NumberFormatException e) {
                System.out.print(
                        "Error: Ingrese un numero entero valido: "
                );
            }
        }
    }

    private static double leerDouble() {
        while (true) {
            String entrada = sc.nextLine().trim();

            try {
                return Double.parseDouble(entrada);

            } catch (NumberFormatException e) {
                System.out.print(
                        "Error: Ingrese un precio numerico valido: "
                );
            }
        }
    }
}