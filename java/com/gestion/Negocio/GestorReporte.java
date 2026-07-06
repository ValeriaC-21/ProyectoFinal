package com.gestion.Negocio;

import com.gestion.Modelo.Falla;
import com.gestion.Modelo.Mantenimiento;
import java.util.List;

public class GestorReporte {

    /** RF7 — Consultar reportes de fallas */
    public void consultarFallas(GestorFallas gestorFallas) {
        List<Falla> lista = gestorFallas.getLista();

        if (lista.isEmpty()) {
            System.out.println("No hay fallas registradas.");
            return;
        }

        System.out.println("\n--- Reporte de fallas ---");
        for (Falla f : lista)
            System.out.println(f); // usa el toString() de Falla
    }

    /** Consultar historial completo de mantenimientos */
    public void consultarMantenimientos(GestorMantenimiento gestorMant) {
        List<Mantenimiento> lista = gestorMant.getLista();

        if (lista.isEmpty()) {
            System.out.println("No hay mantenimientos registrados.");
            return;
        }

        System.out.println("\n--- Historial de mantenimientos ---");
        for (Mantenimiento m : lista)
            System.out.println(m); // usa el toString() de Mantenimiento
    }
}
