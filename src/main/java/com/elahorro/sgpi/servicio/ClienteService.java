package com.elahorro.sgpi.servicio;

import com.elahorro.sgpi.modelo.Cliente;
import com.elahorro.sgpi.repositorio.ClienteDAO;

import java.util.ArrayList;
import java.util.List;

public class ClienteService {

    private final ClienteDAO dao;

    public ClienteService(ClienteDAO dao) {
        this.dao = dao;
    }

    public List<Cliente> listar() {
        return dao.listar();
    }

    public Cliente registrar(String nombre, String dni, String telefono, String direccion) {
        if (buscarPorDni(dni) != null) {
            throw new IllegalArgumentException("Ya existe un cliente con ese DNI (DNI unico).");
        }
        Cliente cliente = new Cliente(siguienteId(), nombre, dni, telefono, direccion);
        dao.insertar(cliente);
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
        dao.actualizar(cliente);
    }

    public void eliminar(Cliente cliente) {
        if (dao.tienePedidos(cliente.getId())) {
            throw new IllegalStateException(
                    "No se puede eliminar el cliente porque tiene pedidos registrados.");
        }
        dao.eliminar(cliente.getId());
    }

    public Cliente buscarPorDni(String dni) {
        List<Cliente> clientes = dao.listar();
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
        List<Cliente> clientes = dao.listar();
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getNombre().toLowerCase().contains(filtro)) {
                encontrados.add(clientes.get(i));
            }
        }
        return encontrados;
    }

    public int siguienteId() {
        int max = 0;
        List<Cliente> clientes = dao.listar();
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getId() > max) {
                max = clientes.get(i).getId();
            }
        }
        return max + 1;
    }
}
