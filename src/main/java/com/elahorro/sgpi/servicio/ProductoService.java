package com.elahorro.sgpi.servicio;

import com.elahorro.sgpi.modelo.Categoria;
import com.elahorro.sgpi.modelo.Producto;
import com.elahorro.sgpi.modelo.Usuario;
import com.elahorro.sgpi.repositorio.ProductoRepositorio;

import java.util.ArrayList;
import java.util.List;

public class ProductoService {

    private final ProductoRepositorio repositorio;
    private final List<Producto> productos;

    public ProductoService(ProductoRepositorio repositorio) {
        this.repositorio = repositorio;
        this.productos = new ArrayList<Producto>(repositorio.cargar());
    }

    public List<Producto> listar() {
        return new ArrayList<Producto>(productos);
    }

    public Producto registrar(String codigo, String nombre, double precio,
                              int stock, int stockMinimo, Categoria categoria) {
        if (buscarPorCodigo(codigo) != null) {
            throw new IllegalArgumentException("Ya existe un producto con ese codigo.");
        }
        Producto producto = new Producto(
                siguienteId(), codigo, nombre, precio, stock, stockMinimo, categoria);
        productos.add(producto);
        guardar();
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
        guardar();
    }

    public void eliminar(Usuario solicitante, Producto producto) {
        if (solicitante == null || !solicitante.esAdministrador()) {
            throw new IllegalStateException(
                    "Solo el administrador puede eliminar productos (regla de negocio).");
        }
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getId() == producto.getId()) {
                productos.remove(i);
                break;
            }
        }
        guardar();
    }

    public List<Producto> buscar(String texto) {
        List<Producto> encontrados = new ArrayList<Producto>();
        String filtro = texto == null ? "" : texto.trim().toLowerCase();
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
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getCodigo().equalsIgnoreCase(codigo)) {
                return productos.get(i);
            }
        }
        return null;
    }

    public Producto buscarPorId(int id) {
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getId() == id) {
                return productos.get(i);
            }
        }
        return null;
    }

    public List<Producto> listarStockBajo() {
        List<Producto> encontrados = new ArrayList<Producto>();
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).esStockBajo()) {
                encontrados.add(productos.get(i));
            }
        }
        return encontrados;
    }

    public int siguienteId() {
        int max = 0;
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getId() > max) {
                max = productos.get(i).getId();
            }
        }
        return max + 1;
    }

    public void guardar() {
        repositorio.guardar(productos);
    }
}
