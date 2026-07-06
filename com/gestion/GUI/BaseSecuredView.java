package com.gestion.GUI;

import com.gestion.Modelo.Usuario;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

import java.util.List;

public abstract class BaseSecuredView extends VerticalLayout implements BeforeEnterObserver {

    protected Usuario getUsuarioActivo() {
        return UiUtils.getUsuarioActivo();
    }

    /**
     * Cargos autorizados a acceder a esta vista.
     * Si una subclase retorna null (comportamiento por defecto) o una lista vacía,
     * cualquier usuario que haya iniciado sesión puede acceder, sin restricción de rol.
     * Las vistas que deban restringirse por rol (Administrador, Técnico de Mantenimiento,
     * Operador) deben sobreescribir este método, por ejemplo:
     *   return List.of(UiUtils.ROL_ADMINISTRADOR);
     */
    protected List<String> getRolesPermitidos() {
        return null;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Usuario usuarioActivo = getUsuarioActivo();

        if (usuarioActivo == null) {
            event.forwardTo(AccesoView.class);
            return;
        }

        List<String> rolesPermitidos = getRolesPermitidos();
        if (rolesPermitidos != null && !rolesPermitidos.isEmpty()) {
            boolean autorizado = rolesPermitidos.stream()
                    .anyMatch(rol -> rol.equalsIgnoreCase(usuarioActivo.getCargo()));
            if (!autorizado) {
                UiUtils.showError("[AVISO] No tiene permisos para acceder a esta sección.");
                event.forwardTo(DashboardView.class);
            }
        }
    }
}
