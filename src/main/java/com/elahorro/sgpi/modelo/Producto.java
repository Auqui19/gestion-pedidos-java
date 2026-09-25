package com.elahorro.sgpi.modelo;

import com.elahorro.sgpi.util.Validador;

/**
 * Producto del inventario. Controla stock y stock minimo para las alertas.
 */
public class Producto implements Identificable, Mostrable {

    private int id;
    private String codigo;
    private String nombre;
    private double precio;
    private int stock;
    private int stockMinimo;
    private Categoria categoria;

    public Producto(int id, String codigo, String nombre, double precio,
                    int stock, int stockMinimo, Categoria categoria) {
        this.id = id;
        setCodigo(codigo);
        setNombre(nombre);
        setPrecio(precio);
        setStock(stock);
        setStockMinimo(stockMinimo);
        setCategoria(categoria);
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        Validador.textoObligatorio(codigo, "codigo");
        this.codigo = codigo.trim().toUpperCase();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        Validador.longitudMinima(nombre, 3, "nombre de producto");
        this.nombre = nombre.trim();
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        Validador.numeroPositivo(precio, "precio");
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        Validador.enteroNoNegativo(stock, "stock");
        this.stock = stock;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        Validador.enteroNoNegativo(stockMinimo, "stock minimo");
        this.stockMinimo = stockMinimo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("La categoria es obligatoria.");
        }
        this.categoria = categoria;
    }

    public boolean hayStock(int cantidad) {
        return cantidad > 0 && stock >= cantidad;
    }

    public void reducirStock(int cantidad) {
        Validador.enteroPositivo(cantidad, "cantidad");
        if (!hayStock(cantidad)) {
            throw new IllegalStateException(
                    "Stock insuficiente para '" + nombre + "'. Disponible: " + stock + ".");
        }
        stock -= cantidad;
    }

    public void aumentarStock(int cantidad) {
        Validador.enteroPositivo(cantidad, "cantidad");
        stock += cantidad;
    }

    public boolean esStockBajo() {
        return stock <= stockMinimo;
    }

    @Override
    public String[] toFila() {
        return new String[]{
                String.valueOf(id),
                codigo,
                nombre,
                String.format("%.2f", precio),
                String.valueOf(stock),
                String.valueOf(stockMinimo),
                String.valueOf(categoria.getId())
        };
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Producto)) {
            return false;
        }
        Producto otro = (Producto) obj;
        return id != 0 && id == otro.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
