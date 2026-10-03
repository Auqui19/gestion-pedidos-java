package com.elahorro.sgpi.servicio;

import com.elahorro.sgpi.modelo.Categoria;
import com.elahorro.sgpi.repositorio.CategoriaDAO;

import java.util.List;

public class CategoriaService {

    private final CategoriaDAO dao;

    public CategoriaService(CategoriaDAO dao) {
        this.dao = dao;
    }

    public List<Categoria> listar() {
        return dao.listar();
    }

    public Categoria registrar(String nombre) {
        if (buscarPorNombre(nombre) != null) {
            throw new IllegalArgumentException("Ya existe una categoria con ese nombre.");
        }
        Categoria categoria = new Categoria(siguienteId(), nombre);
        dao.insertar(categoria);
        return categoria;
    }

    public void actualizar(Categoria categoria, String nombre) {
        Categoria existente = buscarPorNombre(nombre);
        if (existente != null && existente.getId() != categoria.getId()) {
            throw new IllegalArgumentException("Ya existe una categoria con ese nombre.");
        }
        categoria.setNombre(nombre);
        dao.actualizar(categoria);
    }

    public void eliminar(Categoria categoria) {
        if (dao.tieneProductos(categoria.getId())) {
            throw new IllegalStateException(
                    "No se puede eliminar la categoria porque tiene productos asociados.");
        }
        dao.eliminar(categoria.getId());
    }

    public Categoria buscarPorNombre(String nombre) {
        List<Categoria> categorias = dao.listar();
        for (int i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).getNombre().equalsIgnoreCase(nombre)) {
                return categorias.get(i);
            }
        }
        return null;
    }

    public Categoria buscarPorId(int id) {
        return dao.buscarPorId(id).orElse(null);
    }

    public int siguienteId() {
        int max = 0;
        List<Categoria> categorias = dao.listar();
        for (int i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).getId() > max) {
                max = categorias.get(i).getId();
            }
        }
        return max + 1;
    }
}
