package com.gestion.Modelo;

import java.time.LocalDate;

public class Falla {

    private static int contadorId = 1;

    private int id;
    private String idEquipo;
    private String descripcion;
    private String usuarioReporta;
    private LocalDate fechaReporte;
    private String estadoFalla;   // pendiente | en revisión | resuelta

    public Falla(String idEquipo, String descripcion,
                 String usuarioReporta, LocalDate fechaReporte) {
        this.id = contadorId++;
        this.idEquipo = idEquipo;
        this.descripcion = descripcion;
        this.usuarioReporta = usuarioReporta;
        this.fechaReporte = fechaReporte;
        this.estadoFalla = "pendiente"; // estado inicial siempre
    }

    public static int getContadorId() {
        return contadorId;
    }

    public static void setContadorId(int contadorId) {
        Falla.contadorId = contadorId;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUsuarioReporta() {
        return usuarioReporta;
    }

    public void setUsuarioReporta(String usuarioReporta) {
        this.usuarioReporta = usuarioReporta;
    }

    public LocalDate getFechaReporte() {
        return fechaReporte;
    }

    public void setFechaReporte(LocalDate fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    public String getEstadoFalla() {
        return estadoFalla;
    }

    public void setEstadoFalla(String estadoFalla) {
        this.estadoFalla = estadoFalla;
    }

    @Override
    public String toString() {
        return "[FALLA #" + id + "]" +
                " Equipo: "   + idEquipo +
                " | Estado: " + estadoFalla +
                " | Usuario: " + usuarioReporta +
                " | Fecha: "  + fechaReporte +
                " | Desc: "   + descripcion;
    }
}