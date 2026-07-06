package com.gestion.GUI;

import com.gestion.Interfaz.Sistema;
import com.gestion.Modelo.DatosEquipo;
import com.gestion.Modelo.EquipoComputo;
import com.gestion.Modelo.Usuario;
import com.gestion.Modelo.EquipoImpresion;
import com.gestion.Modelo.EquipoProyectore;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.Locale;

@Route(value = "equipos", layout = MainLayout.class)
@PageTitle("Equipos")
public class GestionEquiposView extends BaseSecuredView {
    private final ListDataProvider<DatosEquipo> dataProvider = new ListDataProvider<>(Sistema.gestorEquipo.listarTodos());
    private final Grid<DatosEquipo> grid = new Grid<>(DatosEquipo.class, false);
    private final TextField buscarNombre = new TextField("Buscar por nombre...");
    private final Select<String> filtroEstado = new Select<>();

    public GestionEquiposView() {
        setSizeFull();
        getStyle().set("background-color", UiUtils.COLOR_FONDO);
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.STRETCH);

        H2 titulo = new H2("Equipos. Lista de equipos registrados");
        titulo.getStyle().set("margin", "0");
        titulo.getStyle().set("color", UiUtils.COLOR_TEXTO);

        Button nuevo = new Button("+ Nuevo Equipo");
        UiUtils.stylePrimary(nuevo);
        nuevo.addClickListener(event -> abrirDialogoNuevoEquipo(null));

        HorizontalLayout barraSuperior = new HorizontalLayout();
        barraSuperior.setWidthFull();
        barraSuperior.setSpacing(true);
        barraSuperior.setAlignItems(FlexComponent.Alignment.END);

        buscarNombre.setWidthFull();
        buscarNombre.setPlaceholder("Buscar por nombre...");
        buscarNombre.addValueChangeListener(event -> aplicarFiltros());

        filtroEstado.setLabel("Estado");
        filtroEstado.setItems("Todos", "disponible", "agotado", "operativo", "en mantenimiento", "fuera de servicio");
        filtroEstado.setValue("Todos");
        filtroEstado.addValueChangeListener(event -> aplicarFiltros());

        HorizontalLayout filtros = new HorizontalLayout(buscarNombre, filtroEstado);
        filtros.setWidthFull();
        filtros.expand(buscarNombre);

        barraSuperior.add(filtros, nuevo);
        barraSuperior.expand(filtros);

        configurarGrid();
        grid.setDataProvider(dataProvider);

        add(titulo, barraSuperior, grid);
        expand(grid);
    }

    private void configurarGrid() {
        grid.removeAllColumns();
        grid.addColumn(DatosEquipo::getId).setHeader("ID Equipo").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(DatosEquipo::getNombre).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(DatosEquipo::getTipo).setHeader("Tipo").setAutoWidth(true).setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(equipo ->
                UiUtils.createBadge(equipo.getEstado()))).setHeader("Estado").setAutoWidth(true).setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(equipo -> {
            HorizontalLayout acciones = new HorizontalLayout();
            acciones.setSpacing(true);

            Button btnEditar = UiUtils.iconButton(VaadinIcon.EDIT, UiUtils.COLOR_PRINCIPAL, () -> abrirDialogoNuevoEquipo(equipo));
            Button btnEliminar = UiUtils.iconButton(VaadinIcon.TRASH, UiUtils.COLOR_ERROR, () -> solicitarConfirmacionEliminar(equipo));

            acciones.add(btnEditar, btnEliminar);
            return acciones;
        })).setHeader("Acciones").setAutoWidth(true).setFlexGrow(0);

        grid.setHeight("calc(100vh - 250px)");
    }

    private void aplicarFiltros() {
        String texto = buscarNombre.getValue() == null ? "" : buscarNombre.getValue().trim().toLowerCase(Locale.ROOT);
        String estado = filtroEstado.getValue();
        dataProvider.clearFilters();
        dataProvider.setFilter(equipo -> {
            boolean coincideNombre = (equipo.getNombre() != null && (texto.isBlank() || equipo.getNombre().toLowerCase(Locale.ROOT).contains(texto)));
            boolean coincideEstado = "Todos".equalsIgnoreCase(estado) || estado == null || estado.isBlank() || estado.equalsIgnoreCase(equipo.getEstado());
            return coincideNombre && coincideEstado;
        });
    }

    private void solicitarConfirmacionEliminar(DatosEquipo equipo) {
        Usuario actual = UiUtils.getUsuarioActivo();
        if (actual == null || !"Administrador".equalsIgnoreCase(actual.getCargo())) {
            Notification n = Notification.show("Acceso Denegado: Solo los administradores pueden eliminar equipos.", 3500, Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Confirmación de Baja");

        VerticalLayout layout = new VerticalLayout(
                new Paragraph("¿Está seguro de eliminar el equipo: " + equipo.getNombre() + " (ID: " + equipo.getId() + ")?"),
                new Paragraph("Esta acción es irreversible y requiere su contraseña de Administrador:")
        );

        PasswordField passAdmin = new PasswordField("Contraseña del Administrador");
        passAdmin.setWidthFull();
        layout.add(passAdmin);

        Button btnConfirmar = new Button("Confirmar Eliminación", e -> {
            if (actual.getPassword().equals(passAdmin.getValue())) {
                Sistema.gestorEquipo.eliminarEquipo(equipo.getId());
                dataProvider.refreshAll();
                Notification.show("Equipo eliminado del inventario.", 2500, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialog.close();
            } else {
                Notification.show("Contraseña incorrecta. Autorización denegada.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        btnConfirmar.getStyle().set("background-color", UiUtils.COLOR_ERROR).set("color", "white");

        Button btnCancelar = new Button("Cancelar", e -> dialog.close());
        UiUtils.styleSecondary(btnCancelar);

        dialog.add(layout);
        dialog.getFooter().add(btnCancelar, btnConfirmar);
        dialog.open();
    }

    private void abrirDialogoNuevoEquipo(DatosEquipo equipoAEditar) {
        Dialog dialog = new Dialog();
        dialog.setWidth("820px");
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(true);

        VerticalLayout contenedor = new VerticalLayout();
        contenedor.setPadding(true);
        contenedor.setSpacing(true);
        contenedor.setWidthFull();

        H2 titulo = new H2(equipoAEditar == null ? "Formulario de Nuevo Equipo" : "Modificar Datos del Equipo");
        titulo.getStyle().set("margin", "0");

        Select<String> tipoSelect = new Select<>();
        tipoSelect.setLabel("Tipo de Equipo");
        tipoSelect.setItems("Cómputo", "Impresión", "Proyección");
        tipoSelect.setWidthFull();

        Select<String> subtipoSelect = new Select<>();
        subtipoSelect.setLabel("Subtipo");
        subtipoSelect.setWidthFull();
        subtipoSelect.setEnabled(false);

        TextField idField = new TextField("ID del equipo");
        TextField nombreField = new TextField("Nombre");
        IntegerField cantidadField = new IntegerField("Cantidad");

        idField.setWidthFull();
        nombreField.setWidthFull();
        cantidadField.setWidthFull();

        tipoSelect.addValueChangeListener(event -> {
            String tipo = event.getValue();
            subtipoSelect.clear();
            if ("Cómputo".equals(tipo)) {
                subtipoSelect.setItems(EquipoComputo.getSubTipos());
                subtipoSelect.setEnabled(true);
            } else if ("Impresión".equals(tipo)) {
                subtipoSelect.setItems("Inyección de tinta", "Láser", "Multifuncional");
                subtipoSelect.setEnabled(true);
            } else {
                subtipoSelect.setItems("Genérico", "Estándar");
                subtipoSelect.setEnabled(true);
            }
        });

        if (equipoAEditar != null) {
            tipoSelect.setValue(equipoAEditar.getTipo());
            idField.setValue(equipoAEditar.getId());
            idField.setEnabled(false);
            nombreField.setValue(equipoAEditar.getNombre());
            cantidadField.setValue(equipoAEditar.getCantidad());
        }

        Button cancelar = new Button("Cancelar", event -> dialog.close());
        UiUtils.styleSecondary(cancelar);

        Button guardar = new Button("Guardar");
        UiUtils.stylePrimary(guardar);
        guardar.addClickListener(event -> {
            String tipo = tipoSelect.getValue();
            String subtipo = subtipoSelect.getValue();
            String id = idField.getValue() == null ? "" : idField.getValue().trim();
            String nombre = nombreField.getValue() == null ? "" : nombreField.getValue().trim();
            Integer cantidad = cantidadField.getValue();

            if (tipo == null || id.isBlank() || nombre.isBlank() || cantidad == null) {
                Notification.show("Complete todos los campos para guardar el equipo.", 3500, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            if (equipoAEditar == null) {
                DatosEquipo nuevoEq = crearEquipoPorTipo(tipo, id, nombre, subtipo, cantidad);
                Sistema.gestorEquipo.agregarEquipo(nuevoEq);
            } else {
                equipoAEditar.setNombre(nombre);
                equipoAEditar.setCantidad(cantidad);
                Sistema.gestorEquipo.guardarEquipo(equipoAEditar);
            }

            dataProvider.refreshAll();
            dialog.close();
            Notification.show(equipoAEditar == null ? "Equipo agregado correctamente." : "Equipo modificado correctamente.", 2500, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        HorizontalLayout acciones = new HorizontalLayout(cancelar, guardar);
        acciones.setSpacing(true);
        acciones.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        acciones.setWidthFull();

        contenedor.add(titulo, tipoSelect, subtipoSelect, idField, nombreField, cantidadField, acciones);
        dialog.add(contenedor);
        dialog.open();
    }

    private DatosEquipo crearEquipoPorTipo(String tipo, String id, String nombre, String subtipo, int cantidad) {
        String subtipoSeleccionado = subtipo != null ? subtipo : "";
        return switch (tipo) {
            case "Cómputo" -> new EquipoComputo(id, nombre, subtipoSeleccionado.isBlank() ? "Laptop" : subtipoSeleccionado, cantidad, LocalDate.now());
            case "Impresión" -> new EquipoImpresion(id, nombre, subtipoSeleccionado.isBlank() ? "Multifuncional" : subtipoSeleccionado, cantidad, LocalDate.now());
            case "Proyección" -> new EquipoProyectore(id, nombre, subtipoSeleccionado.isBlank() ? "Proyector" : subtipoSeleccionado, cantidad, LocalDate.now());
            default -> new EquipoComputo(id, nombre, subtipoSeleccionado.isBlank() ? "Laptop" : subtipoSeleccionado, cantidad, LocalDate.now());
        };
    }
}