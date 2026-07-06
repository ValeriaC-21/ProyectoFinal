package com.gestion.Negocio;

import com.gestion.Modelo.DatosEquipo;
import com.gestion.Modelo.EquipoComputo;
import com.gestion.Modelo.EquipoImpresion;
import com.gestion.Modelo.EquipoProyectore;
import com.gestion.Persistencia.MongoDataStore;

import java.util.ArrayList;
import java.util.List;

public class GestorEquipo {

    private final List<DatosEquipo> equipos = new ArrayList<>();
    private final List<EquipoComputo> equipoComputo = new ArrayList<>();
    private final List<EquipoImpresion> equipoImpresion = new ArrayList<>();
    private final List<EquipoProyectore> equipoProyectore = new ArrayList<>();
    private final MongoDataStore dataStore = MongoDataStore.getInstance();

    public GestorEquipo() {
        for (DatosEquipo equipo : dataStore.cargarEquipos()) {
            agregarEnMemoria(equipo);
        }
    }

    public void agregarEquipo(DatosEquipo equipo) {
        if (equipo != null && existeId(equipo.getId())) {
            throw new IllegalStateException(
                    "Ya existe un equipo registrado con el ID '" + equipo.getId() + "'. Utilice un identificador único.");
        }
        guardarEquipo(equipo);
    }

    /** Indica si ya existe un equipo registrado con el ID indicado. */
    public boolean existeId(String id) {
        return id != null && buscarPorId(id) != null;
    }

    public void guardarEquipo(DatosEquipo equipo) {
        if (equipo == null) {
            return;
        }

        equipo.setTipo(normalizarTipo(equipo));
        eliminarEnMemoria(equipo.getId());
        agregarEnMemoria(equipo);
        dataStore.guardarEquipo(equipo);
    }

    /** Devuelve todos los equipos en una sola lista (polimorfismo) */
    public List<DatosEquipo> listarTodos() {
        return equipos;
    }

    public DatosEquipo buscarPorId(String id) {
        for (DatosEquipo e : equipos)
            if (e.getId().equals(id)) return e;
        return null;
    }

    public void actualizarEstado(String id, String nuevoEstado) {
        DatosEquipo datosEquipo = buscarPorId(id);
        if (datosEquipo != null) {
            datosEquipo.setEstado(nuevoEstado);
            dataStore.guardarEquipo(datosEquipo);
        }
    }

    public void eliminarEquipo(String id) {
        DatosEquipo equipo = buscarPorId(id);
        if (equipo == null) {
            return;
        }

        eliminarEnMemoria(id);
        dataStore.eliminarEquipo(id);
    }

    public List<EquipoComputo> getEquipoComputo() {
        return equipoComputo;
    }

    public List<EquipoImpresion> getEquipoImpresion() {
        return equipoImpresion;
    }

    public List<EquipoProyectore> getEquipoProyectore() {
        return equipoProyectore;
    }

    private void agregarEnMemoria(DatosEquipo equipo) {
        equipos.add(equipo);

        if (equipo instanceof EquipoComputo computo) {
            equipoComputo.add(computo);
        } else if (equipo instanceof EquipoImpresion impresion) {
            equipoImpresion.add(impresion);
        } else if (equipo instanceof EquipoProyectore proyector) {
            equipoProyectore.add(proyector);
        }
    }

    private void eliminarEnMemoria(String id) {
        equipos.removeIf(equipo -> equipo.getId().equals(id));
        equipoComputo.removeIf(equipo -> equipo.getId().equals(id));
        equipoImpresion.removeIf(equipo -> equipo.getId().equals(id));
        equipoProyectore.removeIf(equipo -> equipo.getId().equals(id));
    }

    private String normalizarTipo(DatosEquipo equipo) {
        if (equipo instanceof EquipoComputo) {
            return "Cómputo";
        }
        if (equipo instanceof EquipoImpresion) {
            return "Impresión";
        }
        if (equipo instanceof EquipoProyectore) {
            return "Proyección";
        }
        return equipo.getTipo();
    }
}