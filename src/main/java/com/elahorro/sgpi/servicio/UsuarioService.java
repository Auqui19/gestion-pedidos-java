package com.elahorro.sgpi.servicio;

import com.elahorro.sgpi.modelo.Usuario;
import com.elahorro.sgpi.modelo.enums.Rol;
import com.elahorro.sgpi.repositorio.UsuarioRepositorio;

import java.util.ArrayList;
import java.util.List;

public class UsuarioService {

    private final UsuarioRepositorio repositorio;
    private final List<Usuario> usuarios;

    public UsuarioService(UsuarioRepositorio repositorio) {
        this.repositorio = repositorio;
        this.usuarios = new ArrayList<Usuario>(repositorio.cargar());
        if (usuarios.isEmpty()) {
            usuarios.add(Usuario.conPasswordPlano(
                    1, "Administrador", "admin", "admin123", Rol.ADMINISTRADOR));
            guardar();
        }
    }

    public Usuario login(String username, String password) {
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
        return new ArrayList<Usuario>(usuarios);
    }

    public Usuario registrar(Usuario solicitante, String nombre, String username,
                             String password, Rol rol) {
        requerirAdministrador(solicitante);
        if (buscarPorUsername(username) != null) {
            throw new IllegalArgumentException("Ya existe un usuario con ese nombre de usuario.");
        }
        Usuario nuevo = Usuario.conPasswordPlano(siguienteId(), nombre, username, password, rol);
        usuarios.add(nuevo);
        guardar();
        return nuevo;
    }

    public void eliminar(Usuario solicitante, int id) {
        requerirAdministrador(solicitante);
        if (solicitante.getId() == id) {
            throw new IllegalArgumentException("No puede eliminar su propio usuario.");
        }
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId() == id) {
                usuarios.remove(i);
                break;
            }
        }
        guardar();
    }

    public Usuario buscarPorUsername(String username) {
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
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId() > max) {
                max = usuarios.get(i).getId();
            }
        }
        return max + 1;
    }

    public void guardar() {
        repositorio.guardar(usuarios);
    }
}
