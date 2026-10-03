package com.elahorro.sgpi.servicio;

import com.elahorro.sgpi.modelo.Usuario;
import com.elahorro.sgpi.modelo.enums.Rol;
import com.elahorro.sgpi.repositorio.UsuarioDAO;

import java.util.List;

public class UsuarioService {

    private final UsuarioDAO dao;

    public UsuarioService(UsuarioDAO dao) {
        this.dao = dao;
        sembrarAdministradorSiVacio();
    }

    private void sembrarAdministradorSiVacio() {
        if (dao.listar().isEmpty()) {
            dao.insertar(Usuario.conPasswordPlano(
                    1, "Administrador", "admin", "admin123", Rol.ADMINISTRADOR));
        }
    }

    public Usuario login(String username, String password) {
        List<Usuario> usuarios = dao.listar();
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario usuario = usuarios.get(i);
            if (usuario.getUsername().equalsIgnoreCase(username)
                    && usuario.autenticar(password)) {
                return usuario;
            }
        }
        return null;
    }

    public List<Usuario> listar() {
        return dao.listar();
    }

    public Usuario registrar(Usuario solicitante, String nombre, String username,
                             String password, Rol rol) {
        requerirAdministrador(solicitante);
        if (buscarPorUsername(username) != null) {
            throw new IllegalArgumentException("Ya existe un usuario con ese nombre de usuario.");
        }
        Usuario nuevo = Usuario.conPasswordPlano(siguienteId(), nombre, username, password, rol);
        dao.insertar(nuevo);
        return nuevo;
    }

    public void eliminar(Usuario solicitante, int id) {
        requerirAdministrador(solicitante);
        if (solicitante.getId() == id) {
            throw new IllegalArgumentException("No puede eliminar su propio usuario.");
        }
        if (dao.tienePedidos(id)) {
            throw new IllegalStateException(
                    "No se puede eliminar el usuario porque registro pedidos.");
        }
        dao.eliminar(id);
    }

    public Usuario buscarPorUsername(String username) {
        List<Usuario> usuarios = dao.listar();
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario usuario = usuarios.get(i);
            if (usuario.getUsername().equalsIgnoreCase(username)) {
                return usuario;
            }
        }
        return null;
    }

    private void requerirAdministrador(Usuario solicitante) {
        if (solicitante == null || !solicitante.esAdministrador()) {
            throw new IllegalStateException(
                    "Solo el administrador puede gestionar usuarios (regla de negocio).");
        }
    }

    public int siguienteId() {
        int max = 0;
        List<Usuario> usuarios = dao.listar();
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId() > max) {
                max = usuarios.get(i).getId();
            }
        }
        return max + 1;
    }
}
