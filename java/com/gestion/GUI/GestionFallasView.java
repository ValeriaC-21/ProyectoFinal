package com.gestion.GUI;

import com.gestion.Interfaz.Sistema;
import com.gestion.Modelo.DatosEquipo;
import com.gestion.Modelo.Falla;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;

@Route(value = "fallas", layout = MainLayout.class)
@PageTitle("Fallas")
public class GestionFallasView extends BaseSecuredView {

    private final Grid<Falla> grid = new Grid<>(Falla.class, false);

    public GestionFallasView() {
        setSizeFull();
        getStyle().set("background-color", UiUtils.COLOR_FONDO);
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.STRETCH);

        H2 titulo = new H2("Fallas. Registro y control operativo");
        titulo.getStyle().set("margin", "0");
        titulo.getStyle().set("color", UiUtils.COLOR_TEXTO);

        Button btnReportar = new Button("Reportar Nueva Falla", VaadinIcon.WARNING.create());
        UiUtils.stylePrimary(btnReportar);
        btnReportar.addClickListener(e -> abrirModalReportar());

        Button btnActualizar = new Button("Actualizar Estado de Equipo", VaadinIcon.REFRESH.create());
        UiUtils.styleSecondary(btnActualizar);
        btnActualizar.addClickListener(e -> abrirModalActualizar());

        HorizontalLayout barraAcciones = new HorizontalLayout(btnReportar, btnActualizar);
        barraAcciones.setSpacing(true);

        configurarGrid();
        grid.setItems(Sistema.gestorFallas.getLista());

        add(titulo, barraAcciones, grid);
        expand(grid);
    }

    private void configurarGrid() {
        grid.removeAllColumns();
        grid.addColumn(Falla::getId).setHeader("ID").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Falla::getIdEquipo).setHeader("ID Equipo").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(new ComponentRenderer<>(falla -> UiUtils.createBadge(falla.getEstadoFalla()))).setHeader("Estado").setAutoWidth(true);
        grid.addColumn(Falla::getFechaReporte).setHeader("Fecha").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Falla::getUsuarioReporta).setHeader("Usuario").setAutoWidth(true);
        grid.addColumn(Falla::getDescripcion).setHeader("Descripción").setAutoWidth(true);
        grid.setHeight("calc(100vh - 260px)");
    }

    private void abrirModalReportar() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Reportar Nueva Falla");
        dialog.setWidth("500px");

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        Select<DatosEquipo> equipoSelect = new Select<>();
        equipoSelect.setLabel("Equipo");
        equipoSelect.setWidthFull();
        equipoSelect.setItems(Sistema.gestorEquipo.listarTodos());
        equipoSelect.setItemLabelGenerator(equipo -> equipo.getNombre() + " | ID: " + equipo.getId());

        TextArea descripcion = new TextArea("Descripción de la falla");
        descripcion.setWidthFull();
        descripcion.setMinHeight("120px");

        TextField usuario = new TextField("Nombre del usuario que reporta");
        usuario.setWidthFull();

        layout.add(equipoSelect, descripcion, usuario);

        Button guardar = new Button("Guardar");
        UiUtils.stylePrimary(guardar);
        guardar.addClickListener(event -> {
            DatosEquipo equipo = equipoSelect.getValue();
            String desc = descripcion.getValue() == null ? "" : descripcion.getValue().trim();
            String nombreUsuario = usuario.getValue() == null ? "" : usuario.getValue().trim();

            if (equipo == null || desc.isBlank() || nombreUsuario.isBlank()) {
                Notification.show("Complete todos los campos.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            Sistema.gestorFallas.reportar(equipo.getId(), desc, nombreUsuario, LocalDate.now());
            Sistema.gestorEquipo.actualizarEstado(equipo.getId(), "en mantenimiento");
            grid.setItems(Sistema.gestorFallas.getLista());

            Notification.show("Falla registrada. Equipo en mantenimiento.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            dialog.close();
        });

        Button cancelar = new Button("Cancelar", e -> dialog.close());
        UiUtils.styleSecondary(cancelar);

        dialog.add(layout);
        dialog.getFooter().add(cancelar, guardar);
        dialog.open();
    }

    private void abrirModalActualizar() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Actualizar Estado del Equipo");
        dialog.setWidth("500px");

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        Select<DatosEquipo> equipoSelect = new Select<>();
        equipoSelect.setLabel("Equipo");
        equipoSelect.setWidthFull();
        equipoSelect.setItems(Sistema.gestorEquipo.listarTodos());
        equipoSelect.setItemLabelGenerator(equipo -> equipo.getNombre() + " | ID: " + equipo.getId() + " (" + equipo.getEstado() + ")");

        Select<String> nuevoEstado = new Select<>();
        nuevoEstado.setLabel("Nuevo estado");
        nuevoEstado.setItems("operativo", "en mantenimiento", "fuera de servicio");
        nuevoEstado.setWidthFull();

        layout.add(equipoSelect, nuevoEstado);

        Button actualizar = new Button("Actualizar");
        UiUtils.stylePrimary(actualizar);
        actualizar.addClickListener(event -> {
            DatosEquipo equipo = equipoSelect.getValue();
            String estadoSeleccionado = nuevoEstado.getValue();

            if (equipo == null || estadoSeleccionado == null) {
                Notification.show("Seleccione equipo y nuevo estado.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            Sistema.gestorEquipo.actualizarEstado(equipo.getId(), estadoSeleccionado);
            Sistema.gestorFallas.actualizarEstado(equipo.getId(), estadoSeleccionado);
            grid.setItems(Sistema.gestorFallas.getLista());

            Notification.show("Estado actualizado.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            dialog.close();
        });

        Button cancelar = new Button("Cancelar", e -> dialog.close());
        UiUtils.styleSecondary(cancelar);

        dialog.add(layout);
        dialog.getFooter().add(cancelar, actualizar);
        dialog.open();
    }
}