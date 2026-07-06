package com.gestion.GUI;

import com.gestion.Interfaz.Sistema;
import com.gestion.Modelo.Usuario;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("Acceso al Sistema")
public class AccesoView extends VerticalLayout {

    private final TextField cedulaField = new TextField("Cédula / ID");
    private final PasswordField passwordField = new PasswordField("Contraseña"); // <-- SE INTEGRA LA CONTRASEÑA
    private final Button loginButton = new Button("Iniciar Sesión");
    private final Button focusLoginButton = new Button("Iniciar Sesión");
    private final Button registrarseButton = new Button("Registrarse");

    public AccesoView() {
        setSizeFull();
        getStyle().set("background-color", UiUtils.COLOR_FONDO);
        getStyle().set("color", UiUtils.COLOR_TEXTO);
        setPadding(true);
        setSpacing(false);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        HorizontalLayout contenedor = new HorizontalLayout();
        contenedor.setWidthFull();
        contenedor.setMaxWidth("1280px");
        contenedor.setSpacing(true);
        contenedor.setPadding(false);
        contenedor.setAlignItems(Alignment.STRETCH);

        VerticalLayout bienvenidaCard = construirBienvenida();
        VerticalLayout loginCard = construirLogin();

        contenedor.add(bienvenidaCard, loginCard);
        contenedor.expand(bienvenidaCard, loginCard);
        add(contenedor);
    }

    private VerticalLayout construirBienvenida() {
        VerticalLayout card = UiUtils.createCard();
        card.setWidthFull();
        card.setPadding(true);
        card.setSpacing(true);
        card.setJustifyContentMode(JustifyContentMode.CENTER);
        card.setAlignItems(Alignment.START);
        card.getStyle().set("background", "linear-gradient(180deg, #EFF6FF 0%, white 100%)");
        card.getStyle().set("min-height", "640px");

        Span badge = new Span("Sistema de mantenimiento");
        badge.getStyle().set("background-color", UiUtils.toPastel(UiUtils.COLOR_PRINCIPAL));
        badge.getStyle().set("color", UiUtils.COLOR_PRINCIPAL);
        badge.getStyle().set("padding", "0.35rem 0.7rem");
        badge.getStyle().set("border-radius", "999px");
        badge.getStyle().set("font-weight", "700");

        H1 titulo = new H1("Sistema de Gestión de Mantenimiento de Equipos Tecnológicos");
        titulo.getStyle().set("margin", "0");
        titulo.getStyle().set("color", UiUtils.COLOR_TEXTO);

        Paragraph subtitulo = new Paragraph("Organiza, controla y da seguimiento al mantenimiento de tus equipos tecnológicos.");
        subtitulo.getStyle().set("font-size", "1.05rem");
        subtitulo.getStyle().set("color", UiUtils.COLOR_TEXTO);

        focusLoginButton.addClickListener(event -> cedulaField.focus());
        UiUtils.stylePrimary(focusLoginButton);

        registrarseButton.addClickListener(event -> UI.getCurrent().navigate("registro-usuario"));
        UiUtils.styleSecondary(registrarseButton);

        HorizontalLayout acciones = new HorizontalLayout(focusLoginButton, registrarseButton);
        acciones.setSpacing(true);

        Paragraph pie = new Paragraph("© 2026 Sistema de Mantenimiento Versión 1.0.0");
        pie.getStyle().set("margin-top", "auto");
        pie.getStyle().set("color", "#6B7280");

        card.add(badge, titulo, subtitulo, acciones, pie);
        return card;
    }

    private VerticalLayout construirLogin() {
        VerticalLayout card = UiUtils.createCard();
        card.setWidthFull();
        card.getStyle().set("padding", "2rem");
        card.getStyle().set("display", "flex");
        card.getStyle().set("flex-direction", "column");
        card.getStyle().set("justify-content", "center");
        card.getStyle().set("min-height", "640px");

        H1 titulo = new H1("Iniciar Sesión");
        titulo.getStyle().set("margin", "0 0 0.5rem 0");

        Paragraph subtitulo = new Paragraph("Ingrese sus credenciales de cuenta para acceder.");
        subtitulo.getStyle().set("margin", "0 0 1.5rem 0");

        cedulaField.setPlaceholder("Ej: 1712345678");
        cedulaField.setWidthFull();
        cedulaField.getStyle().set("margin-bottom", "1rem");

        passwordField.setPlaceholder("Ingrese su contraseña");
        passwordField.setWidthFull();
        passwordField.getStyle().set("margin-bottom", "1.5rem");

        loginButton.addClickListener(event -> iniciarSesion());
        UiUtils.stylePrimary(loginButton);
        loginButton.setWidthFull();

        card.add(titulo, subtitulo, cedulaField, passwordField, loginButton);
        return card;
    }

    private void iniciarSesion() {
        String cedula = cedulaField.getValue().trim();
        String password = passwordField.getValue();

        if (cedula.isEmpty() || password.isEmpty()) {
            UiUtils.showError("[AVISO] Por favor, ingrese tanto la cédula como la contraseña.");
            return;
        }

        try {
            Sistema sistema = new Sistema();
            Usuario usuario = sistema.gestorUsuario.buscarPorCedula(cedula);

            // Se valida la existencia del usuario y la coincidencia de su contraseña
            if (usuario != null && password.equals(usuario.getPassword())) {
                UiUtils.setUsuarioActivo(usuario);
                UiUtils.showSuccess("¡Bienvenido al sistema, " + usuario.getNombre() + "!");
                UI.getCurrent().navigate("dashboard");
            } else {
                UiUtils.showError("[AVISO] Credenciales inválidas. Verifique la cédula o contraseña.");
            }
        } catch (Exception e) {
            UiUtils.showError("Error en el sistema: " + e.getMessage());
        }
    }
}