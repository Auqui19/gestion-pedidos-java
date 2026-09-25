package com.elahorro.sgpi.servicio;

import com.elahorro.sgpi.modelo.Categoria;
import com.elahorro.sgpi.repositorio.CategoriaRepositorio;

import java.util.ArrayList;
import java.util.List;

public class CategoriaService {

    private final CategoriaRepositorio repositorio;
    private final List<Categoria> categorias;

    public CategoriaService(CategoriaRepositorio repositorio) {
        this.repositorio = repositorio;
        this.categorias = new ArrayList<Categoria>(repositorio.cargar());
    }

    public List<Categoria> listar() {
        return new ArrayList<Categoria>(categorias);
    }

    public Categoria registrar(String nombre) {
        if (buscarPorNombre(nombre) != null) {
            throw new IllegalArgumentException("Ya existe una categoria con ese nombre.");
        }
        Categoria categoria = new Categoria(siguienteId(), nombre);
        categorias.add(categoria);
        guardar();
        return categoria;
    }

    public void actualizar(Categoria categoria, String nombre) {
        Categoria existente = buscarPorNombre(nombre);
        if (existente != null && existente.getId() != categoria.getId()) {
            throw new IllegalArgumentException("Ya existe una categoria con ese nombre.");
        }
        categoria.setNombre(nombre);
        guardar();
    }

    public void eliminar(Categoria categoria) {
        for (int i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).getId() == categoria.getId()) {
                categorias.remove(i);
                break;
            }
        }
        guardar();
    }

    public Categoria buscarPorNombre(String nombre) {
        for (int i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).getNombre().equalsIgnoreCase(nombre)) {
                return categorias.get(i);
            }
        }
        return null;
    }

    public Categoria buscarPorId(int id) {
        for (int i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).getId() == id) {
                return categorias.get(i);
            }
        }
        return null;
    }

    public int siguienteId() {
        int max = 0;
        for (int i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).getId() > max) {
                max = categorias.get(i).getId();
            }
        }
        return max + 1;
    }

    public void guardar() {
        repositorio.guardar(categorias);
    }
}
