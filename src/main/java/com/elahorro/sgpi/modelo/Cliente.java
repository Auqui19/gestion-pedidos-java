package com.elahorro.sgpi.modelo;

import com.elahorro.sgpi.util.Validador;

/**
 * Cliente del minimarket. Hereda de Persona. El DNI es unico (RN-03).
 */
public class Cliente extends Persona implements Mostrable {

    private String dni;
    private String telefono;
    private String direccion;

    public Cliente(int id, String nombre, String dni, String telefono, String direccion) {
        super(id, nombre);
        setDni(dni);
        setTelefono(telefono);
        setDireccion(direccion);
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        Validador.dni(dni);
        this.dni = dni.trim();
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        Validador.textoObligatorio(telefono, "telefono");
        this.telefono = telefono.trim();
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        Validador.textoObligatorio(direccion, "direccion");
        this.direccion = direccion.trim();
    }

    @Override
    public String[] toFila() {
        return new String[]{
                String.valueOf(getId()),
                getNombre(),
                dni,
                telefono,
                direccion
        };
    }

    @Override
    public String toString() {
        return getNombre() + " (DNI " + dni + ")";
    }
}
