package com.elahorro.sgpi.servicio;

import com.elahorro.sgpi.modelo.Cliente;
import com.elahorro.sgpi.repositorio.ClienteRepositorio;

import java.util.ArrayList;
import java.util.List;

public class ClienteService {

    private final ClienteRepositorio repositorio;
    private final List<Cliente> clientes;

    public ClienteService(ClienteRepositorio repositorio) {
        this.repositorio = repositorio;
        this.clientes = new ArrayList<Cliente>(repositorio.cargar());
    }

    public List<Cliente> listar() {
        return new ArrayList<Cliente>(clientes);
    }

    public Cliente registrar(String nombre, String dni, String telefono, String direccion) {
        if (buscarPorDni(dni) != null) {
            throw new IllegalArgumentException("Ya existe un cliente con ese DNI (DNI unico).");
        }
        Cliente cliente = new Cliente(siguienteId(), nombre, dni, telefono, direccion);
        clientes.add(cliente);
        guardar();
        return cliente;
    }

    public void actualizar(Cliente cliente, String nombre, String dni,
                           String telefono, String direccion) {
        Cliente existente = buscarPorDni(dni);
        if (existente != null && existente.getId() != cliente.getId()) {
            throw new IllegalArgumentException("Ya existe un cliente con ese DNI (DNI unico).");
        }
        cliente.setNombre(nombre);
        cliente.setDni(dni);
        cliente.setTelefono(telefono);
        cliente.setDireccion(direccion);
        guardar();
    }

    public void eliminar(Cliente cliente) {
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getId() == cliente.getId()) {
                clientes.remove(i);
                break;
            }
        }
        guardar();
    }

    public Cliente buscarPorDni(String dni) {
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getDni().equals(dni)) {
                return clientes.get(i);
            }
        }
        return null;
    }

    public List<Cliente> buscarPorNombre(String texto) {
        List<Cliente> encontrados = new ArrayList<Cliente>();
        String filtro = texto == null ? "" : texto.trim().toLowerCase();
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getNombre().toLowerCase().contains(filtro)) {
                encontrados.add(clientes.get(i));
            }
        }
        return encontrados;
    }

    public int siguienteId() {
        int max = 0;
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getId() > max) {
                max = clientes.get(i).getId();
            }
        }
        return max + 1;
    }

    public void guardar() {
        repositorio.guardar(clientes);
    }
}
