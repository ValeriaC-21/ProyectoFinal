package com.gestion.GUI;

import com.gestion.Modelo.Usuario;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

public abstract class BaseSecuredView extends VerticalLayout implements BeforeEnterObserver {

    protected Usuario getUsuarioActivo() {
        return UiUtils.getUsuarioActivo();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (getUsuarioActivo() == null) {
            event.forwardTo(AccesoView.class);
        }
    }
}
