package com.gestion.GUI;

import com.gestion.Modelo.Usuario;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    private final Span usuarioLabel = new Span();

    // Referencias a las pestañas para poder mostrarlas/ocultarlas según el rol del usuario activo
    private Tab tabUsuarios;
    private Tab tabEquipos;
    private Tab tabMantenimiento;
    private Tab tabFallas;
    private Tab tabReportes;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        configurarDrawer();
        configurarNavbar();
        aplicarVisibilidadPorRol();
    }

    private void configurarDrawer() {
        // Se amplía el ancho del panel lateral para que íconos y textos no se corten al navegar
        getElement().getStyle().set("--vaadin-app-layout-drawer-width", "272px");

        VerticalLayout drawer = new VerticalLayout();
        drawer.setPadding(true);
        drawer.setSpacing(false);
        drawer.setWidthFull();
        drawer.setHeightFull();
        drawer.getStyle().set("background-color", UiUtils.COLOR_PRINCIPAL);
        drawer.getStyle().set("color", "white");
        drawer.getStyle().set("overflow", "visible");
        drawer.getStyle().set("box-sizing", "border-box");

        HorizontalLayout logo = new HorizontalLayout();
        logo.setWidthFull();
        logo.setAlignItems(FlexComponent.Alignment.CENTER);
        logo.setSpacing(true);

        Icon logoIcon = VaadinIcon.COG.create();
        logoIcon.setSize("28px");
        logoIcon.getStyle().set("color", "white");
        logoIcon.getStyle().set("flex-shrink", "0");

        H3 titulo = new H3("Gestión Tech");
        titulo.getStyle().set("margin", "0");
        titulo.getStyle().set("color", "white");
        titulo.getStyle().set("white-space", "nowrap");

        logo.add(logoIcon, titulo);

        Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.setWidthFull();
        tabs.getStyle().set("overflow", "visible");
        tabs.getStyle().set("box-sizing", "border-box");

        Tab tabInicio = crearTabNavegacion("Inicio", VaadinIcon.HOME, DashboardView.class);
        tabUsuarios = crearTabNavegacion("Usuarios", VaadinIcon.USERS, GestionUsuariosView.class);
        tabEquipos = crearTabNavegacion("Equipos", VaadinIcon.DESKTOP, GestionEquiposView.class);
        tabMantenimiento = crearTabNavegacion("Mantenimiento", VaadinIcon.TOOLS, GestionMantenimientoView.class);
        tabFallas = crearTabNavegacion("Fallas", VaadinIcon.WARNING, GestionFallasView.class);
        tabReportes = crearTabNavegacion("Reportes", VaadinIcon.CHART, ReportesView.class);
        Tab tabSalir = crearTabSalir();

        tabs.add(tabInicio, tabUsuarios, tabEquipos, tabMantenimiento, tabFallas, tabReportes, tabSalir);

        drawer.add(logo, tabs);
        drawer.expand(tabs);
        addToDrawer(drawer);
    }

    /**
     * Oculta las pestañas de la barra lateral según el cargo del usuario que inició sesión.
     * - Administrador: acceso total (todas las pestañas).
     * - Técnico de Mantenimiento: sin acceso a Usuarios, Equipos ni Reportes.
     * - Operador: sin acceso a Usuarios, Mantenimiento ni Reportes (Equipos queda visible solo
     *   en modo consulta, la vista de Equipos oculta internamente las acciones de edición).
     */
    private void aplicarVisibilidadPorRol() {
        Usuario usuario = UiUtils.getUsuarioActivo();

        boolean esAdministrador = UiUtils.tieneRol(usuario, UiUtils.ROL_ADMINISTRADOR);
        boolean esTecnico = UiUtils.tieneRol(usuario, UiUtils.ROL_TECNICO);
        boolean esOperador = UiUtils.tieneRol(usuario, UiUtils.ROL_OPERADOR);

        tabUsuarios.setVisible(esAdministrador);
        tabEquipos.setVisible(esAdministrador || esOperador);
        tabMantenimiento.setVisible(esAdministrador || esTecnico);
        tabFallas.setVisible(esAdministrador || esTecnico || esOperador);
        tabReportes.setVisible(esAdministrador);
    }

    private void configurarNavbar() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(false);
        header.setSpacing(true);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        header.getStyle().set("padding", "0.75rem 1rem");

        usuarioLabel.getStyle().set("color", UiUtils.COLOR_TEXTO);
        usuarioLabel.getStyle().set("font-weight", "700");
        refrescarUsuarioActivo();

        Span onlineDot = new Span();
        onlineDot.getStyle().set("width", "10px");
        onlineDot.getStyle().set("height", "10px");
        onlineDot.getStyle().set("display", "inline-block");
        onlineDot.getStyle().set("border-radius", "50%");
        onlineDot.getStyle().set("background-color", UiUtils.COLOR_EXITO);

        Span onlineLabel = new Span("En línea");
        onlineLabel.getStyle().set("font-weight", "600");
        onlineLabel.getStyle().set("color", UiUtils.COLOR_TEXTO);

        HorizontalLayout estado = new HorizontalLayout(onlineDot, onlineLabel);
        estado.setSpacing(true);
        estado.setAlignItems(FlexComponent.Alignment.CENTER);

        header.add(usuarioLabel, estado);
        addToNavbar(header);
    }

    private Tab crearTabNavegacion(String texto, VaadinIcon icono, Class<? extends Component> destino) {
        Icon icon = icono.create();
        icon.setSize("18px");
        icon.getStyle().set("color", "white");
        icon.getStyle().set("flex-shrink", "0");

        Span label = new Span(texto);
        label.getStyle().set("color", "white");
        label.getStyle().set("font-weight", "600");
        label.getStyle().set("white-space", "nowrap");
        label.getStyle().set("overflow", "visible");

        HorizontalLayout contenido = new HorizontalLayout(icon, label);
        contenido.setSpacing(true);
        contenido.setAlignItems(FlexComponent.Alignment.CENTER);
        contenido.setWidthFull();
        contenido.getStyle().set("overflow", "visible");
        contenido.getStyle().set("padding", "0.15rem 0.35rem");
        contenido.getStyle().set("box-sizing", "border-box");

        RouterLink link = new RouterLink("", destino);
        link.add(contenido);
        link.getStyle().set("text-decoration", "none");
        link.getStyle().set("width", "100%");
        link.getStyle().set("display", "block");

        Tab tab = new Tab(link);
        tab.getStyle().set("width", "100%");
        tab.getStyle().set("border-radius", "12px");
        tab.getStyle().set("margin", "0.15rem 0");
        tab.getStyle().set("background-color", "rgba(255,255,255,0.12)");
        tab.getStyle().set("overflow", "visible");
        tab.getStyle().set("box-sizing", "border-box");
        tab.getStyle().set("min-width", "0");
        return tab;
    }

    private Tab crearTabSalir() {
        Icon icon = VaadinIcon.SIGN_OUT.create();
        icon.setSize("18px");
        icon.getStyle().set("color", "white");
        icon.getStyle().set("flex-shrink", "0");

        Span label = new Span("Salir");
        label.getStyle().set("color", "white");
        label.getStyle().set("font-weight", "600");
        label.getStyle().set("white-space", "nowrap");

        Button salir = new Button("Salir", icon, event -> abrirDialogoCerrarSesion());
        salir.getStyle().set("width", "100%");
        salir.getStyle().set("background", "transparent");
        salir.getStyle().set("border", "none");
        salir.getStyle().set("box-shadow", "none");
        salir.getStyle().set("padding", "0.45rem 0.25rem");
        salir.getStyle().set("color", "white");
        salir.getStyle().set("white-space", "nowrap");

        Tab tab = new Tab(salir);
        tab.getStyle().set("width", "100%");
        tab.getStyle().set("border-radius", "12px");
        tab.getStyle().set("margin", "0.15rem 0");
        tab.getStyle().set("background-color", "rgba(255,255,255,0.12)");
        tab.getStyle().set("overflow", "visible");
        tab.getStyle().set("box-sizing", "border-box");
        return tab;
    }

    private void refrescarUsuarioActivo() {
        Usuario usuario = UiUtils.getUsuarioActivo();
        usuarioLabel.setText(usuario == null ? "Sin sesión activa" : UiUtils.nombreConCargo(usuario));
    }

    private void abrirDialogoCerrarSesion() {
        Dialog confirmacion = new Dialog();
        confirmacion.setCloseOnOutsideClick(false);
        confirmacion.setCloseOnEsc(false);
        confirmacion.getElement().getStyle().set("background-color", "white");
        confirmacion.getElement().getStyle().set("border-radius", "18px");

        VerticalLayout contenido = new VerticalLayout();
        contenido.setPadding(true);
        contenido.setSpacing(true);
        contenido.setAlignItems(FlexComponent.Alignment.CENTER);

        Icon icono = VaadinIcon.LOCK.create();
        icono.setSize("56px");
        icono.getStyle().set("color", UiUtils.COLOR_PRINCIPAL);

        H3 titulo = new H3("¿Está seguro que desea cerrar sesión?");
        titulo.getStyle().set("margin", "0");
        titulo.getStyle().set("color", UiUtils.COLOR_TEXTO);
        titulo.getStyle().set("text-align", "center");

        Button cancelar = new Button("Cancelar", event -> confirmacion.close());
        UiUtils.styleSecondary(cancelar);

        Button salir = new Button("Salir", event -> {
            confirmacion.close();
            UiUtils.limpiarSesion();
            mostrarDialogoSalida();
        });
        UiUtils.styleDanger(salir);

        HorizontalLayout acciones = new HorizontalLayout(cancelar, salir);
        acciones.setSpacing(true);
        acciones.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        contenido.add(icono, titulo, acciones);
        confirmacion.add(contenido);
        confirmacion.open();
    }

    private void mostrarDialogoSalida() {
        Dialog exitoso = new Dialog();
        exitoso.setCloseOnEsc(true);
        exitoso.setCloseOnOutsideClick(false);
        exitoso.getElement().getStyle().set("background-color", "white");
        exitoso.getElement().getStyle().set("border-radius", "18px");

        VerticalLayout contenido = new VerticalLayout();
        contenido.setPadding(true);
        contenido.setSpacing(true);
        contenido.setAlignItems(FlexComponent.Alignment.CENTER);

        Icon icono = VaadinIcon.CHECK_CIRCLE.create();
        icono.setSize("56px");
        icono.getStyle().set("color", UiUtils.COLOR_EXITO);

        Span mensaje = new Span("Sesión finalizada. ¡Hasta pronto!");
        mensaje.getStyle().set("color", UiUtils.COLOR_TEXTO);
        mensaje.getStyle().set("font-weight", "700");
        mensaje.getStyle().set("text-align", "center");

        Button aceptar = new Button("Aceptar", event -> {
            exitoso.close();
            UI.getCurrent().navigate(AccesoView.class);
        });
        UiUtils.stylePrimary(aceptar);

        contenido.add(icono, mensaje, aceptar);
        exitoso.add(contenido);
        exitoso.open();
    }
}