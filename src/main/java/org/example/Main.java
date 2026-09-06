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
                case 7 -> System.out.println("\n¡Hasta luego!");
                default -> System.out.println("Error: Opción no válida. Intente nuevamente.");
            }
        } while (opcion != 7);
    }

    private static void mostrarMenu() {
        System.out.println("\n=== SISTEMA DE GESTIÓN DE PEDIDOS ===");
        System.out.println("1. Registrar producto");
        System.out.println("2. Registrar cliente");
        System.out.println("3. Registrar pedido");
        System.out.println("4. Consultar productos");
        System.out.println("5. Consultar clientes");
        System.out.println("6. Consultar pedidos");
        System.out.println("7. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static void registrarProducto() {
        System.out.println("\n=== REGISTRO DE PRODUCTO ===");
        System.out.print("Ingrese nombre del producto: ");
        String nombre = sc.nextLine();

        System.out.print("Ingrese precio del producto: ");
        double precio = leerDouble();

        try {
            Producto producto = new Producto(nombre, precio);
            productos.add(producto);
            System.out.println("Producto registrado correctamente.");
            producto.mostrarDatos();
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void registrarCliente() {
        System.out.println("\n=== REGISTRO DE CLIENTE ===");
        System.out.print("Ingrese nombre: ");
        String nombre = sc.nextLine();

        if (nombre.trim().isEmpty()) {
            System.out.println("Error: El nombre no puede estar vacio.");
            return;
        }

        System.out.print("Ingrese DNI: ");
        String dni = sc.nextLine();

        if (!dni.matches("\\d{8}")) {
            System.out.println("Error: El DNI debe contener exactamente 8 numeros.");
            return;
        }

        Cliente cliente = new Cliente(nombre, dni);
        clientes.add(cliente);
        System.out.println("Cliente registrado correctamente.");
        cliente.mostrarDatos();
    }

    private static void registrarPedido() {
        if (productos.isEmpty()) {
            System.out.println("Error: No hay productos registrados. Registre un producto primero.");
            return;
        }
        if (clientes.isEmpty()) {
            System.out.println("Error: No hay clientes registrados. Registre un cliente primero.");
            return;
        }

        System.out.println("\n=== REGISTRO DE PEDIDO ===");

        consultarClientes();
        System.out.print("Seleccione el número del cliente: ");
        int idxCliente = leerEntero() - 1;
        if (idxCliente < 0 || idxCliente >= clientes.size()) {
            System.out.println("Error: Cliente no válido.");
            return;
        }
        Cliente cliente = clientes.get(idxCliente);

        consultarProductos();
        System.out.print("Seleccione el número del producto: ");
        int idxProducto = leerEntero() - 1;
        if (idxProducto < 0 || idxProducto >= productos.size()) {
            System.out.println("Error: Producto no válido.");
            return;
        }
        Producto producto = productos.get(idxProducto);

        System.out.print("Ingrese cantidad: ");
        int cantidad = leerEntero();

        try {
            Pedido pedido = new Pedido(producto, cantidad);
            pedidos.add(pedido);
            System.out.println("Pedido registrado correctamente.");
            System.out.println("Cliente: " + cliente.getNombre() + " (DNI: " + cliente.getDni() + ")");
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
            Producto p = productos.get(i);
            System.out.printf("%d. %s - S/ %.2f%n", i + 1, p.getNombre(), p.getPrecio());
        }
    }

    private static void consultarClientes() {
        System.out.println("\n=== LISTA DE CLIENTES ===");
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        for (int i = 0; i < clientes.size(); i++) {
            Cliente c = clientes.get(i);
            System.out.printf("%d. %s (DNI: %s)%n", i + 1, c.getNombre(), c.getDni());
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
            System.out.println("Pedido " + (i + 1) + ":");
            pedido.mostrarPedido();
            System.out.println("----------------------------------");
            totalGeneral += pedido.calcularTotal();
        }
        System.out.printf("Importe total de todos los pedidos: S/ %.2f%n", totalGeneral);
    }

    private static int leerEntero() {
        while (!sc.hasNextInt()) {
            System.out.println("Error: Ingrese un número válido.");
            sc.next();
        }
        int valor = sc.nextInt();
        sc.nextLine();
        return valor;
    }

    private static double leerDouble() {
        while (!sc.hasNextDouble()) {
            System.out.println("Error: Ingrese un número válido.");
            sc.next();
        }
        double valor = sc.nextDouble();
        sc.nextLine();
        return valor;
    }
}