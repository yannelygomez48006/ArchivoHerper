package com.example.appsqliteopenhelper;

public class Articulo {

    String codigo;
    String descripcion;
    double precio;
    byte[] imagen;

    public Articulo(String codigo, String descripcion, double precio, byte[] imagen) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagen = imagen;
    }

    public Articulo(String codigo, String descripcion, double precio) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecio() {
        return precio;
    }
    public byte[] getImagen() {
        return imagen;
    }


    @Override
    public String toString() {
        return "Codigo: " + codigo + "\n" +
                "Descripcion: " + descripcion + "\n" +
                "Precio: " + precio + "\n";
    }

}
