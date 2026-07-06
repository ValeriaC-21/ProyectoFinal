package com.gestion.Modelo;

import java.time.LocalDate;

public class Mantenimiento {

    private static int contadorId = 1; // genera IDs automáticos

    private int id;
    private String idEquipo;
    private String tipo;          // Preventivo | Correctivo | Diagnóstico
    private String descripcion;
    private String tecnicoResponsable;
    private LocalDate fecha;

    public Mantenimiento(String idEquipo, String tipo,
                         String descripcion, String tecnico,
                         LocalDate fecha) {
        this.id = contadorId++;
        this.idEquipo = idEquipo;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.tecnicoResponsable = tecnico;
        this.fecha = fecha;
    }

    public static int getContadorId() {
        return contadorId;
    }

    public static void setContadorId(int contadorId) {
        Mantenimiento.contadorId = contadorId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(String idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTecnicoResponsable() {
        return tecnicoResponsable;
    }

    public void setTecnicoResponsable(String tecnicoResponsable) {
        this.tecnicoResponsable = tecnicoResponsable;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "[MANTENIMIENTO #" + id + "]" +
                " Equipo: "  + idEquipo +
                " | Tipo: "   + tipo +
                " | Técnico: " + tecnicoResponsable +
                " | Fecha: "  + fecha +
                " | Desc: "   + descripcion;
    }
}
