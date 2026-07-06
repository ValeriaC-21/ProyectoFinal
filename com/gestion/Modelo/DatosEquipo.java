package com.gestion.Modelo;

import java.time.LocalDate;

public abstract class DatosEquipo {
    private String id;
    private String nombre;
    private String tipo;
    private int cantidad;
    private String estado;
    private LocalDate fechaIngreso;

    public DatosEquipo(String id, String nombre, String tipo, int cantidad,
                       LocalDate fechaIngreso) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.cantidad = cantidad;
        if (cantidad > 0) {
            this.estado = "disponible";
        } else {
            this.estado = "agotado";
        }
        this.fechaIngreso = fechaIngreso;
    }

    public abstract String obtenerFicha();


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    @Override
    public String toString(){
        return "Tipo: " + tipo + " | ID: " + id + " | Nombre: " + nombre +
                " | Cantidad: " + cantidad + "\n";
    }
}
