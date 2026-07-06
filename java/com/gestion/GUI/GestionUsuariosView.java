package com.gestion.GUI;

import com.gestion.Exception.UsuarioDuplicadoException;
import com.gestion.Interfaz.Sistema;
import com.gestion.Modelo.Usuario;
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
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Locale;

@Route(value = "usuarios", layout = MainLayout.class)
@PageTitle("Gestión de Usuarios")
public class GestionUsuariosView extends BaseSecuredView {

    private final ListDataProvider<Usuario> dataProvider = new ListDataProvider<>(Sistema.gestorUsuario.listarTodos());
    private final Grid<Usuario> grid = new Grid<>(Usuario.class, false);
    private final TextField buscarNombre = new TextField();

    public GestionUsuariosView() {
        setSizeFull();
        getStyle().set("background-color", UiUtils.COLOR_FONDO);
        setPadding(true);
        setSpacing(true);

        // ---- ENCABEZADO (Según Sección 5 del Mockup) ----
        H2 titulo = new H2("Usuarios");
        titulo.getStyle().set("margin", "0");
        titulo.getStyle().set("color", UiUtils.COLOR_TEXTO);

        Paragraph sub = new Paragraph("Lista de usuarios registrados");
        sub.getStyle().set("margin", "0");

        Button btnNuevo = new Button("+ Nuevo Usuario");
        UiUtils.stylePrimary(btnNuevo);
        btnNuevo.addClickListener(event -> abrirDialogoUsuario(null));

        // ---- BARRA DE BÚSQUEDA ----
        buscarNombre.setPlaceholder("Buscar usuario...");
        buscarNombre.setWidth("300px");
        buscarNombre.addValueChangeListener(event -> aplicarFiltros());

        HorizontalLayout barraSuperior = new HorizontalLayout(buscarNombre, btnNuevo);
        barraSuperior.setWidthFull();
        barraSuperior.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        barraSuperior.setAlignItems(FlexComponent.Alignment.CENTER);

        // ---- CONFIGURACIÓN DE LA TABLA (GRID) ----
        configurarGrid();
        grid.setDataProvider(dataProvider);

        add(titulo, sub, barraSuperior, grid);
        expand(grid);
    }

    private void configurarGrid() {
        grid.removeAllColumns();
        grid.addColumn(Usuario::getCedulaId).setHeader("Cédula").setAutoWidth(true);
        grid.addColumn(Usuario::getNombre).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Usuario::getCargo).setHeader("Cargo").setAutoWidth(true);
        grid.addColumn(Usuario::getTelefono).setHeader("Teléfono").setAutoWidth(true);

        // Columna de Acciones (Lápiz y Basurero idénticos a tu maqueta)
        grid.addColumn(new ComponentRenderer<>(usuario -> {
            HorizontalLayout acciones = new HorizontalLayout();
            acciones.setSpacing(true);

            // Botón Editar (Azul)
            Button btnEditar = UiUtils.iconButton(VaadinIcon.EDIT, UiUtils.COLOR_PRINCIPAL, () -> abrirDialogoUsuario(usuario));

            // Botón Eliminar (Rojo)
            Button btnEliminar = UiUtils.iconButton(VaadinIcon.TRASH, UiUtils.COLOR_ERROR, () -> solicitarConfirmacionEliminar(usuario));

            acciones.add(btnEditar, btnEliminar);
            return acciones;
        })).setHeader("Acciones").setAutoWidth(true).setFlexGrow(0);

        grid.setHeight("calc(100vh - 260px)");
    }

    private void aplicarFiltros() {
        String texto = buscarNombre.getValue() == null ? "" : buscarNombre.getValue().trim().toLowerCase(Locale.ROOT);
        dataProvider.clearFilters();
        dataProvider.setFilter(user -> user.getNombre() != null && user.getNombre().toLowerCase(Locale.ROOT).contains(texto));
    }

    // ---- LÓGICA DE ELIMINACIÓN CON SEGURIDAD (Solo Administradores) ----
    private void solicitarConfirmacionEliminar(Usuario usuarioAEliminar) {
        Usuario actual = UiUtils.getUsuarioActivo();

        // Regla de negocio: Solo un Administrador da de baja
        if (actual == null || !"Administrador".equalsIgnoreCase(actual.getCargo())) {
            Notification.show("Acceso Denegado: Solo un Administrador puede eliminar usuarios.", 3500, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        // Evitar que el administrador se elimine a sí mismo
        if (actual.getCedulaId().equals(usuarioAEliminar.getCedulaId())) {
            Notification.show("Operación No Permitida: No puedes eliminar tu propia cuenta en sesión.", 3500, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Confirmación de Baja de Usuario");

        VerticalLayout layout = new VerticalLayout(
                new Paragraph("¿Está seguro de eliminar al usuario: " + usuarioAEliminar.getNombre() + "?"),
                new Paragraph("Esta acción revocará sus accesos de forma permanente.")
        );

        PasswordField passAdmin = new PasswordField("Contraseña del Administrador para Autorizar");
        passAdmin.setWidthFull();
        layout.add(passAdmin);

        Button btnConfirmar = new Button("Confirmar Eliminación", e -> {
            if (actual.getPassword().equals(passAdmin.getValue())) {
                try {
                    Sistema.gestorUsuario.eliminarUsuario(usuarioAEliminar.getCedulaId());
                } catch (Exception ex) {
                    Notification.show("No se pudo eliminar el usuario: " + ex.getMessage(), 3500, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }
                dataProvider.refreshAll();
                Notification.show("Usuario dado de baja del sistema.", 2500, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialog.close();
            } else {
                Notification.show("Contraseña de confirmación incorrecta.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        btnConfirmar.getStyle().set("background-color", UiUtils.COLOR_ERROR).set("color", "white");

        Button btnCancelar = new Button("Cancelar", e -> dialog.close());
        UiUtils.styleSecondary(btnCancelar);

        dialog.add(layout);
        dialog.getFooter().add(btnCancelar, btnConfirmar);
        dialog.open();
    }

    // ---- DIÁLOGO EMERGENTE PARA CREAR O EDITAR USUARIO ----
    private void abrirDialogoUsuario(Usuario usuarioAEditar) {
        Dialog dialog = new Dialog();
        dialog.setWidth("460px");
        dialog.setCloseOnOutsideClick(false);

        VerticalLayout contenedor = new VerticalLayout();
        contenedor.setPadding(true);
        contenedor.setSpacing(true);

        H2 tituloDialogo = new H2(usuarioAEditar == null ? "Registrar Nuevo Usuario" : "Modificar Datos de Usuario");
        tituloDialogo.getStyle().set("margin", "0").set("font-size", "20px");

        TextField cedulaField = new TextField("Cédula / ID");
        TextField nombreField = new TextField("Nombre Completo");
        Select<String> cargoSelect = new Select<>();
        cargoSelect.setLabel("Cargo / Rol");
        cargoSelect.setItems("Administrador", "Técnico de Mantenimiento", "Operador");
        TextField telefonoField = new TextField("Teléfono");
        PasswordField passField = new PasswordField("Contraseña");

        cedulaField.setWidthFull();
        nombreField.setWidthFull();
        cargoSelect.setWidthFull();
        telefonoField.setWidthFull();
        passField.setWidthFull();

        // Si se va a editar, precargamos los datos y bloqueamos la cédula (llave primaria)
        if (usuarioAEditar != null) {
            cedulaField.setValue(usuarioAEditar.getCedulaId());
            cedulaField.setEnabled(false);
            nombreField.setValue(usuarioAEditar.getNombre());
            cargoSelect.setValue(usuarioAEditar.getCargo());
            telefonoField.setValue(usuarioAEditar.getTelefono());
            passField.setValue(usuarioAEditar.getPassword());
        }

        Button btnCancelar = new Button("Cancelar", event -> dialog.close());
        UiUtils.styleSecondary(btnCancelar);

        Button btnGuardar = new Button("Guardar");
        UiUtils.stylePrimary(btnGuardar);
        btnGuardar.addClickListener(event -> {
            String cedula = cedulaField.getValue() == null ? "" : cedulaField.getValue().trim();
            String nombre = nombreField.getValue() == null ? "" : nombreField.getValue().trim();
            String cargo = cargoSelect.getValue();
            String telefono = telefonoField.getValue() == null ? "" : telefonoField.getValue().trim();
            String pass = passField.getValue() == null ? "" : passField.getValue().trim();

            if (cedula.isBlank() || nombre.isBlank() || cargo == null || pass.isBlank()) {
                Notification.show("Complete todos los campos obligatorios.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            if (usuarioAEditar == null) {
                // Instanciación limpia respetando el orden exacto de tu backend (cedula, nombre, cargo, telefono, contrasenia)
                Usuario nuevo = new Usuario(cedula, nombre, cargo, telefono, pass);
                try {
                    Sistema.gestorUsuario.registrarUsuario(nuevo);
                } catch (UsuarioDuplicadoException e) {
                    Notification.show("El usuario con esta cédula ya existe.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    throw new RuntimeException(e);
                }
            } else {
                // Mutamos el objeto existente directamente sobre el listado indexado
                usuarioAEditar.setNombre(nombre);
                usuarioAEditar.setCargo(cargo);
                usuarioAEditar.setTelefono(telefono);
                usuarioAEditar.setPassword(pass);
                try {
                    Sistema.gestorUsuario.modificarUsuario(usuarioAEditar);
                } catch (Exception ex) {
                    Notification.show("No se pudo actualizar el usuario: " + ex.getMessage(), 3500, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }
            }

            dataProvider.refreshAll();
            dialog.close();
            Notification.show(usuarioAEditar == null ? "Usuario creado con éxito." : "Usuario actualizado con éxito.", 2500, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        HorizontalLayout acciones = new HorizontalLayout(btnCancelar, btnGuardar);
        acciones.setWidthFull();
        acciones.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        acciones.setSpacing(true);

        contenedor.add(tituloDialogo, cedulaField, nombreField, cargoSelect, telefonoField, passField, acciones);
        dialog.add(contenedor);
        dialog.open();
    }
}