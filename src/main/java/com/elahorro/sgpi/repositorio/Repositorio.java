package com.elahorro.sgpi.repositorio;

import com.elahorro.sgpi.modelo.Identificable;

import java.util.List;
import java.util.Optional;

/**
 * Contrato generico de persistencia (patron DAO / Repository).
 * Los identificadores se asignan en memoria (siguienteId) y se insertan de
 * forma explicita, por lo que las entidades conservan ids inmutables.
 */
public interface Repositorio<T extends Identificable> {

    List<T> listar();

    void insertar(T entidad);

    void actualizar(T entidad);

    void eliminar(int id);

    default Optional<T> buscarPorId(int id) {
        for (T entidad : listar()) {
            if (entidad.getId() == id) {
                return Optional.of(entidad);
            }
        }
        return Optional.empty();
    }
}
