package com.elahorro.sgpi.servicio;

import com.elahorro.sgpi.modelo.Categoria;
import com.elahorro.sgpi.modelo.Producto;
import com.elahorro.sgpi.modelo.Usuario;
import com.elahorro.sgpi.repositorio.ProductoDAO;

import java.util.ArrayList;
import java.util.List;

public class ProductoService {

    private final ProductoDAO dao;

    public ProductoService(ProductoDAO dao) {
        this.dao = dao;
    }

    public List<Producto> listar() {
        return dao.listar();
    }

    public Producto registrar(String codigo, String nombre, double precio,
                              int stock, int stockMinimo, Categoria categoria) {
        if (buscarPorCodigo(codigo) != null) {
            throw new IllegalArgumentException("Ya existe un producto con ese codigo.");
        }
        Producto producto = new Producto(
                siguienteId(), codigo, nombre, precio, stock, stockMinimo, categoria);
        dao.insertar(producto);
        return producto;
    }

    public void actualizar(Producto producto, String codigo, String nombre, double precio,
                           int stock, int stockMinimo, Categoria categoria) {
        Producto existente = buscarPorCodigo(codigo);
        if (existente != null && existente.getId() != producto.getId()) {
            throw new IllegalArgumentException("Ya existe un producto con ese codigo.");
        }
        producto.setCodigo(codigo);
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setStockMinimo(stockMinimo);
        producto.setCategoria(categoria);
        dao.actualizar(producto);
    }

    public void eliminar(Usuario solicitante, Producto producto) {
        if (solicitante == null || !solicitante.esAdministrador()) {
            throw new IllegalStateException(
                    "Solo el administrador puede eliminar productos (regla de negocio).");
        }
        if (dao.tieneDetalles(producto.getId())) {
            throw new IllegalStateException(
                    "No se puede eliminar el producto porque aparece en pedidos.");
        }
        dao.eliminar(producto.getId());
    }

    /** Persiste el stock actual de todos los productos que lo requieran. */
    public void guardarStock(List<Producto> productos) {
        for (int i = 0; i < productos.size(); i++) {
            dao.actualizarStock(productos.get(i));
        }
    }

    public List<Producto> buscar(String texto) {
        List<Producto> encontrados = new ArrayList<Producto>();
        String filtro = texto == null ? "" : texto.trim().toLowerCase();
        List<Producto> productos = dao.listar();
        for (int i = 0; i < productos.size(); i++) {
            Producto producto = productos.get(i);
            if (producto.getNombre().toLowerCase().contains(filtro)
                    || producto.getCodigo().toLowerCase().contains(filtro)) {
                encontrados.add(producto);
            }
        }
        return encontrados;
    }

    public Producto buscarPorCodigo(String codigo) {
        List<Producto> productos = dao.listar();
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getCodigo().equalsIgnoreCase(codigo)) {
                return productos.get(i);
            }
        }
        return null;
    }

    public Producto buscarPorId(int id) {
        return dao.buscarPorId(id).orElse(null);
    }

    public List<Producto> listarStockBajo() {
        List<Producto> encontrados = new ArrayList<Producto>();
        List<Producto> productos = dao.listar();
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).esStockBajo()) {
                encontrados.add(productos.get(i));
            }
        }
        return encontrados;
    }

    public int siguienteId() {
        int max = 0;
        List<Producto> productos = dao.listar();
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getId() > max) {
                max = productos.get(i).getId();
            }
        }
        return max + 1;
    }
}
