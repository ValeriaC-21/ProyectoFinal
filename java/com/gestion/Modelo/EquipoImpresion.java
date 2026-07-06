package com.gestion.Modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EquipoImpresion extends DatosEquipo{

    private static final List<String> subTipos = new ArrayList<>();

    static {
            subTipos.add("Impresora de tinta");
            subTipos.add("Impresora láser");
            subTipos.add("Multifuncional");
            subTipos.add("Escáner");
    }


    private String subTipo;



    public EquipoImpresion(String id, String nombre, String subTipos,
                           int cantidad, LocalDate fechaIngreso) {
        super(id, nombre, "Impresion", cantidad, fechaIngreso);
        this.subTipo=subTipos;
    }

    @Override
    public String obtenerFicha() {
        return "[IMPRESIÓN] " + getNombre() +
                " | Subtipo: " + subTipo +
                " | Estado: " + getEstado() +
                " | Cant.: " + getCantidad();
    }

    public static List<String> getSubTipos(){
        return subTipos;
    }
    public String getSubTipo() {
        return subTipo;
    }

    public void setSubTipo(String subTipos) {
        this.subTipo = subTipos;
    }
}
