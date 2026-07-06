package com.gestion.Modelo;

import java.time.LocalDate;

public class Falla {

    private static int contadorId = 1;

    private String id;
    private String idEquipo;
    private String descripcion;
    private String usuarioReporta;
    private String cedulaUsuarioReporta; // Cédula del usuario que reporta (identificador único, evita ambigüedad por nombres repetidos)
    private LocalDate fechaReporte;
    private String estadoFalla;   // pendiente | en revisión | resuelta

    /** Constructor original: se mantiene intacto por compatibilidad con código existente. La cédula queda como null. */
    public Falla(String idEquipo, String descripcion,
                 String usuarioReporta, LocalDate fechaReporte) {
        this(idEquipo, descripcion, usuarioReporta, null, fechaReporte);
    }

    /** Constructor extendido: permite asociar la cédula del usuario que reporta la falla. */
    public Falla(String idEquipo, String descripcion, String usuarioReporta,
                 String cedulaUsuarioReporta, LocalDate fechaReporte) {
        this.id = String.valueOf(contadorId++);
        this.idEquipo = idEquipo;
        this.descripcion = descripcion;
        this.usuarioReporta = usuarioReporta;
        this.cedulaUsuarioReporta = cedulaUsuarioReporta;
        this.fechaReporte = fechaReporte;
        this.estadoFalla = "pendiente"; // estado inicial siempre
    }

    public static int getContadorId() {
        return contadorId;
    }

    public static void setContadorId(int contadorId) {
        Falla.contadorId = contadorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getCedulaUsuarioReporta() {
        return cedulaUsuarioReporta;
    }

    public void setCedulaUsuarioReporta(String cedulaUsuarioReporta) {
        this.cedulaUsuarioReporta = cedulaUsuarioReporta;
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