package com.elahorro.sgpi.modelo;

import com.elahorro.sgpi.modelo.enums.MetodoPago;
import com.elahorro.sgpi.util.Validador;

import java.time.LocalDate;

/**
 * Pago de un pedido. El monto y la fecha quedan inmutables (RN-05).
 */
public class Pago implements Identificable, Mostrable {

    private final int id;
    private final LocalDate fecha;
    private final double monto;
    private final MetodoPago metodo;

    public Pago(int id, LocalDate fecha, double monto, MetodoPago metodo) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha del pago es obligatoria.");
        }
        if (metodo == null) {
            throw new IllegalArgumentException("El metodo de pago es obligatorio.");
        }
        Validador.numeroPositivo(monto, "monto");
        this.id = id;
        this.fecha = fecha;
        this.monto = monto;
        this.metodo = metodo;
    }

    @Override
    public int getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public double getMonto() {
        return monto;
    }

    public MetodoPago getMetodo() {
        return metodo;
    }

    @Override
    public String[] toFila() {
        return new String[]{
                String.valueOf(id),
                fecha.toString(),
                String.format("%.2f", monto),
                metodo.name()
        };
    }

    @Override
    public String toString() {
        return metodo + " S/ " + String.format("%.2f", monto);
    }
}
