package com.elahorro.sgpi.modelo;

import com.elahorro.sgpi.modelo.enums.Rol;
import com.elahorro.sgpi.util.Cifrador;
import com.elahorro.sgpi.util.Validador;

/**
 * Usuario del sistema SGPI. Hereda de Persona y participa en el login
 * con roles (Administrador, Vendedor, Almacenero).
 */
public class Usuario extends Persona implements Mostrable {

    private String username;
    private String passwordHash;
    private Rol rol;

    public Usuario(int id, String nombre, String username, String passwordHash, Rol rol) {
        super(id, nombre);
        setUsername(username);
        Validador.textoObligatorio(passwordHash, "password");
        if (rol == null) {
            throw new IllegalArgumentException("El rol es obligatorio.");
        }
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    public static Usuario conPasswordPlano(int id, String nombre, String username,
                                           String password, Rol rol) {
        return new Usuario(id, nombre, username, Cifrador.hash(password), rol);
    }

    public boolean autenticar(String password) {
        return password != null && passwordHash.equals(Cifrador.hash(password));
    }

    public boolean esAdministrador() {
        return rol == Rol.ADMINISTRADOR;
    }

    public boolean puedeEliminar() {
        return esAdministrador();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        Validador.longitudMinima(username, 3, "username");
        this.username = username.trim();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPassword(String passwordPlano) {
        Validador.longitudMinima(passwordPlano, 4, "password");
        this.passwordHash = Cifrador.hash(passwordPlano);
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol es obligatorio.");
        }
        this.rol = rol;
    }

    @Override
    public String[] toFila() {
        return new String[]{
                String.valueOf(getId()),
                getNombre(),
                username,
                passwordHash,
                rol.name()
        };
    }

    @Override
    public String toString() {
        return getNombre() + " (" + username + " - " + rol + ")";
    }
}
