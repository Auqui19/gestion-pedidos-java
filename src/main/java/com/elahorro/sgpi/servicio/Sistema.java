package com.elahorro.sgpi.servicio;

import com.elahorro.sgpi.repositorio.CategoriaDAO;
import com.elahorro.sgpi.repositorio.ClienteDAO;
import com.elahorro.sgpi.repositorio.PagoDAO;
import com.elahorro.sgpi.repositorio.PedidoDAO;
import com.elahorro.sgpi.repositorio.ProductoDAO;
import com.elahorro.sgpi.repositorio.UsuarioDAO;

/**
 * Contexto de aplicacion: crea los DAO (persistencia MySQL) y los servicios,
 * y los conecta. Es el punto de acceso que usa la interfaz de consola (vista).
 */
public class Sistema {

    public final CategoriaService categorias;
    public final ProductoService productos;
    public final ClienteService clientes;
    public final UsuarioService usuarios;
    public final PedidoService pedidos;
    public final ReporteService reportes;

    public Sistema() {
        CategoriaDAO categoriaDAO = new CategoriaDAO();
        ProductoDAO productoDAO = new ProductoDAO();
        ClienteDAO clienteDAO = new ClienteDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        PagoDAO pagoDAO = new PagoDAO();
        PedidoDAO pedidoDAO = new PedidoDAO(clienteDAO, usuarioDAO, productoDAO, pagoDAO);

        categorias = new CategoriaService(categoriaDAO);
        productos = new ProductoService(productoDAO);
        clientes = new ClienteService(clienteDAO);
        usuarios = new UsuarioService(usuarioDAO);
        pedidos = new PedidoService(pedidoDAO, productos, productoDAO, pagoDAO);
        reportes = new ReporteService(pedidos, productos);

        sembrarDatosIniciales();
    }

    private void sembrarDatosIniciales() {
        if (categorias.listar().isEmpty()) {
            categorias.registrar("Celulares");
            categorias.registrar("Laptops");
            categorias.registrar("Accesorios");
            categorias.registrar("Audio");
        }
        if (clientes.listar().isEmpty()) {
            clientes.registrar("Cliente Generico", "00000000", "000000000", "Sin direccion");
        }
    }
}
