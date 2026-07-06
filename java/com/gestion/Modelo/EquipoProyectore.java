package com.gestion.Modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EquipoProyectore extends DatosEquipo{

    public static final List<String> subtipos = new ArrayList<>();

    static {
        subtipos.add("Proyector");
        subtipos.add("Pantalla inteligente");
        subtipos.add("TV");
    }

    private String subTipo;

    public EquipoProyectore(String id, String nombre, String subTipo,
                            int cantidad, LocalDate fechaIngreso) {
        super(id, nombre, "Proyector", cantidad, fechaIngreso);
        this.subTipo=subTipo;
    }

    @Override
    public String obtenerFicha() {
        return "[PROYECCIÓN] " + getNombre() +
                " | Subtipo: " + subTipo +
                " | Estado: " + getEstado() +
                " | Cant.: " + getCantidad();
    }

    public static List<String> getSubtipos() {
        return subtipos;
    }

    public String getSubTipo() {
        return subTipo;
    }

    public void setSubTipo(String subTipo) {
        this.subTipo = subTipo;
    }
}
