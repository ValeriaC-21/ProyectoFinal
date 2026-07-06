package com.gestion.Modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EquipoComputo extends DatosEquipo {

    /** Lista de subtipos disponibles para este tipo de equipo */
    private static final List<String> subTipos = new ArrayList<>();

    static {
       subTipos.add("Computadora de escritorio");
        subTipos.add("Laptop");
        subTipos.add("Tablet");
    }

    private String subtipo; // uno de los valores de SUBTIPOS

    public EquipoComputo(String id, String nombre, String subtipo,
                         int cantidad, LocalDate fechaIngreso) {
        super(id, nombre, "Cómputo", cantidad, fechaIngreso);
        this.subtipo = subtipo;
    }

    @Override
    public String obtenerFicha() {
        return "[CÓMPUTO] " + getNombre() +
                " | Subtipo: " + subtipo +
                " | Estado: " + getEstado() +
                " | Cant.: " + getCantidad();
    }

    public static List<String> getSubTipos() {
        return subTipos;
    }


    public String getSubtipo() {
        return subtipo;
    }

    public void setSubtipo(String subtipo) {
        this.subtipo = subtipo;
    }
}
