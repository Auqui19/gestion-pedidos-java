package com.elahorro.sgpi.servicio;

import com.elahorro.sgpi.repositorio.CategoriaRepositorio;
import com.elahorro.sgpi.repositorio.ClienteRepositorio;
import com.elahorro.sgpi.repositorio.PagoRepositorio;
import com.elahorro.sgpi.repositorio.PedidoRepositorio;
import com.elahorro.sgpi.repositorio.ProductoRepositorio;
import com.elahorro.sgpi.repositorio.UsuarioRepositorio;

/**
 * Contexto de aplicacion: crea los repositorios y servicios, y los conecta.
 * Es el punto de acceso que usa la interfaz grafica (vista).
 */
public class Sistema {

    public final CategoriaService categorias;
    public final ProductoService productos;
    public final ClienteService clientes;
    public final UsuarioService usuarios;
    public final PedidoService pedidos;
    public final ReporteService reportes;

    public Sistema() {
        CategoriaRepositorio categoriaRepositorio = new CategoriaRepositorio();
        ProductoRepositorio productoRepositorio = new ProductoRepositorio(categoriaRepositorio);
        ClienteRepositorio clienteRepositorio = new ClienteRepositorio();
        UsuarioRepositorio usuarioRepositorio = new UsuarioRepositorio();
        PagoRepositorio pagoRepositorio = new PagoRepositorio();
        PedidoRepositorio pedidoRepositorio = new PedidoRepositorio();

        categorias = new CategoriaService(categoriaRepositorio);
        productos = new ProductoService(productoRepositorio);
        clientes = new ClienteService(clienteRepositorio);
        usuarios = new UsuarioService(usuarioRepositorio);
        pedidos = new PedidoService(pedidoRepositorio, productos, pagoRepositorio, clientes, usuarios);
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
