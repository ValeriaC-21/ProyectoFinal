package com.gestion.Negocio;

import com.gestion.Modelo.Falla;
import com.gestion.Persistencia.MongoDataStore;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestorFallas {

    private final List<Falla> lista = new ArrayList<>();
    private final MongoDataStore dataStore = MongoDataStore.getInstance();

    public GestorFallas() {
        lista.addAll(dataStore.cargarFallas());
    }

    /** RF5 — Reportar una falla */
    public void reportar(String idEquipo, String descripcion,
                         String usuario, LocalDate fecha) {
        Falla falla = new Falla(idEquipo, descripcion, usuario, fecha);
        lista.add(falla);
        dataStore.guardarFalla(falla);
    }

    /** RF6 — Actualizar estado de una falla por ID de equipo */
    public void actualizarEstado(String idEquipo, String nuevoEstado) {
        for (Falla falla : lista) {
            if (falla.getIdEquipo().equals(idEquipo)) {
                falla.setEstadoFalla(nuevoEstado);
                dataStore.guardarFalla(falla);
            }
        }
    }

    /** Devuelve toda la lista (útil para GestorReportes) */
    public List<Falla> getLista() {
        return lista;
    }
}
