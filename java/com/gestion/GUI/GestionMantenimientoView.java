package com.gestion.GUI;

import com.gestion.Exception.DatoInvalidoException;
import com.gestion.Interfaz.Sistema;
import com.gestion.Modelo.DatosEquipo;
import com.gestion.Modelo.Mantenimiento;
import com.gestion.Modelo.Usuario;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;

@Route(value = "mantenimiento", layout = MainLayout.class)
@PageTitle("Mantenimiento")
public class GestionMantenimientoView extends BaseSecuredView {

    private final ListDataProvider<Mantenimiento> dataProvider = new ListDataProvider<>(Sistema.gestorMant.getLista());
    private final Grid<Mantenimiento> grid = new Grid<>(Mantenimiento.class, false);

    public GestionMantenimientoView() {
        setSizeFull();
        getStyle().set("background-color", UiUtils.COLOR_FONDO);
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.STRETCH);

        H2 titulo = new H2("Mantenimiento. Registro y consulta");
        titulo.getStyle().set("margin", "0");
        titulo.getStyle().set("color", UiUtils.COLOR_TEXTO);

        Button nuevo = new Button("+ Nuevo Mantenimiento");
        UiUtils.stylePrimary(nuevo);
        nuevo.addClickListener(event -> abrirDialogoMantenimiento());

        HorizontalLayout cabecera = new HorizontalLayout();
        cabecera.setWidthFull();
        cabecera.setAlignItems(FlexComponent.Alignment.END);
        cabecera.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        cabecera.add(nuevo);

        configurarGrid();
        grid.setDataProvider(dataProvider);

        add(titulo, cabecera, grid);
        expand(grid);
    }

    private void configurarGrid() {
        grid.removeAllColumns();
        grid.addColumn(Mantenimiento::getId).setHeader("ID").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Mantenimiento::getIdEquipo).setHeader("ID Equipo").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Mantenimiento::getTipo).setHeader("Tipo").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Mantenimiento::getFecha).setHeader("Fecha").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Mantenimiento::getDescripcion).setHeader("Descripción").setAutoWidth(true);
        grid.addColumn(Mantenimiento::getTecnicoResponsable).setHeader("Técnico Responsable").setAutoWidth(true);
        grid.setHeight("calc(100vh - 250px)");
    }

    private void abrirDialogoMantenimiento() {
        Dialog dialog = new Dialog();
        dialog.setWidth("860px");
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(true);
        dialog.getElement().getStyle().set("background-color", "white");

        VerticalLayout contenedor = new VerticalLayout();
        contenedor.setPadding(true);
        contenedor.setSpacing(true);
        contenedor.setWidthFull();

        H2 titulo = new H2("Registrar nuevo mantenimiento");
        titulo.getStyle().set("margin", "0");
        titulo.getStyle().set("color", UiUtils.COLOR_TEXTO);

        Select<DatosEquipo> equipoSelect = new Select<>();
        equipoSelect.setLabel("Equipo");
        equipoSelect.setWidthFull();
        equipoSelect.setItems(Sistema.gestorEquipo.listarTodos());
        equipoSelect.setItemLabelGenerator(equipo -> equipo.getNombre() + " | ID: " + equipo.getId() + " | Estado: " + equipo.getEstado());

        Select<String> tipoSelect = new Select<>();
        tipoSelect.setLabel("Tipo de Mantenimiento");
        tipoSelect.setItems("Preventivo", "Correctivo", "Diagnóstico");
        tipoSelect.setWidthFull();

        TextArea descripcion = new TextArea("Descripción");
        descripcion.setWidthFull();
        descripcion.setMinHeight("120px");

        TextField cedulaTec = new TextField("Cédula del Técnico responsable");
        cedulaTec.setWidthFull();
        cedulaTec.setPlaceholder("Ingrese la cédula del técnico");

        Button cancelar = new Button("Cancelar", event -> dialog.close());
        UiUtils.styleSecondary(cancelar);

        Button guardar = new Button("Guardar");
        UiUtils.stylePrimary(guardar);
        guardar.addClickListener(event -> {
            try {
                DatosEquipo equipoSeleccionado = equipoSelect.getValue();
                String tipo = tipoSelect.getValue();
                String desc = descripcion.getValue() == null ? "" : descripcion.getValue().trim();
                String cedula = cedulaTec.getValue() == null ? "" : cedulaTec.getValue().trim();

                if (equipoSeleccionado == null || tipo == null || desc.isBlank() || cedula.isBlank()) {
                    throw new DatoInvalidoException("Debe completar todos los campos del mantenimiento.");
                }

                Usuario tecnico = Sistema.gestorUsuario.buscarPorCedula(cedula);
                if (tecnico == null) {
                    throw new DatoInvalidoException("El técnico con cédula " + cedula + " no está registrado. Registrelo primero en el módulo 1.");
                }

                Sistema.gestorMant.registrar(equipoSeleccionado.getId(), tipo, desc, tecnico.getNombre(), LocalDate.now());
                Sistema.gestorEquipo.actualizarEstado(equipoSeleccionado.getId(), "en mantenimiento");
                dataProvider.refreshAll();
                dialog.close();

                Notification notification = Notification.show(
                        "✓ Mantenimiento asignado correctamente al técnico: " + tecnico.getNombre() + ". Equipo " + equipoSeleccionado.getNombre() + " marcado como: en mantenimiento",
                        5000,
                        Notification.Position.MIDDLE
                );
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (DatoInvalidoException e) {
                Notification notification = Notification.show(e.getMessage(), 5000, Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        HorizontalLayout acciones = new HorizontalLayout(cancelar, guardar);
        acciones.setSpacing(true);
        acciones.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        acciones.setWidthFull();

        contenedor.add(titulo, equipoSelect, tipoSelect, descripcion, cedulaTec, acciones);
        dialog.add(contenedor);
        dialog.open();
    }
}

