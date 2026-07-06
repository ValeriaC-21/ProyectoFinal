package com.gestion.GUI;

import com.gestion.Interfaz.Sistema;
import com.gestion.Modelo.Usuario;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("registro-usuario")
@PageTitle("Registro de Usuario")
public class RegistroUsuarioForm extends VerticalLayout {

    private final TextField cedulaField = new TextField("Cédula / ID (10 dígitos)");
    private final TextField nombreField = new TextField("Nombre Completo (Solo letras)");
    private final ComboBox<String> cargoField = new ComboBox<>("Cargo / Rol");
    private final TextField telefonoField = new TextField("Número de Celular (10 dígitos)");
    private final PasswordField passwordField = new PasswordField("Establecer Contraseña");

    private final Button registrarButton = new Button("Registrar");
    private final Button cancelarButton = new Button("Cancelar");

    public RegistroUsuarioForm() {
        setSizeFull();
        getStyle().set("background-color", UiUtils.COLOR_FONDO);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        VerticalLayout card = UiUtils.createCard();
        card.setWidth("460px");
        card.getStyle().set("padding", "2.5rem");

        H1 titulo = new H1("Crear una Cuenta");
        titulo.getStyle().set("margin", "0 0 0.25rem 0");
        titulo.getStyle().set("font-size", "1.75rem");

        Paragraph subtitulo = new Paragraph("Registre sus datos y asigne una contraseña de acceso.");
        subtitulo.getStyle().set("margin", "0 0 1.5rem 0");
        subtitulo.getStyle().set("color", "#6B7280");

        // 1. Control de Cédula (10 dígitos)
        cedulaField.setWidthFull();
        cedulaField.setPlaceholder("Ej: 1712345678");
        cedulaField.setValueChangeMode(ValueChangeMode.EAGER);
        cedulaField.addValueChangeListener(e -> {
            String val = cedulaField.getValue().trim();
            if (val.length() != 10 || !val.matches("\\d+")) {
                cedulaField.setErrorMessage("La cédula debe contener exactamente 10 dígitos numéricos.");
                cedulaField.setInvalid(true);
            } else {
                cedulaField.setInvalid(false);
            }
        });

        // 2. VALIDACIÓN DE SOLO LETRAS EN EL NOMBRE
        nombreField.setWidthFull();
        nombreField.setPlaceholder("Ej: Carlos Andrade");
        nombreField.setValueChangeMode(ValueChangeMode.EAGER);
        nombreField.addValueChangeListener(e -> {
            String val = nombreField.getValue().trim();
            // Solo permite letras a-z, A-Z, tildes, ñ y espacios
            if (!val.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+")) {
                nombreField.setErrorMessage("El nombre solo debe contener caracteres alfabéticos (letras).");
                nombreField.setInvalid(true); // Se pone rojo dinámicamente en Vaadin
            } else if (val.isEmpty()) {
                nombreField.setErrorMessage("El campo nombre no puede quedar vacío.");
                nombreField.setInvalid(true);
            } else {
                nombreField.setErrorMessage(null);
                nombreField.setInvalid(false);
            }
        });

        // 3. ComboBox de Cargos
        cargoField.setWidthFull();
        cargoField.setItems("Técnico", "Administrador", "Operador");
        cargoField.setPlaceholder("Seleccione su cargo");

        // 4. Control de Teléfono (10 dígitos)
        telefonoField.setWidthFull();
        telefonoField.setPlaceholder("Ej: 0998765432");
        telefonoField.setValueChangeMode(ValueChangeMode.EAGER);
        telefonoField.addValueChangeListener(e -> {
            String val = telefonoField.getValue().trim();
            if (val.length() != 10 || !val.matches("\\d+")) {
                telefonoField.setErrorMessage("El teléfono debe tener exactamente 10 dígitos numéricos.");
                telefonoField.setInvalid(true);
            } else {
                telefonoField.setInvalid(false);
            }
        });

        // 5. Campo para ingresar la Contraseña
        passwordField.setWidthFull();
        passwordField.setPlaceholder("Ingrese su contraseña segura");

        // Estilos de botones usando la clase de utilidades
        UiUtils.stylePrimary(registrarButton);
        registrarButton.setWidthFull();
        registrarButton.addClickListener(e -> ejecutarRegistro());

        UiUtils.styleSecondary(cancelarButton);
        cancelarButton.setWidthFull();
        cancelarButton.addClickListener(e -> UI.getCurrent().navigate(""));

        VerticalLayout acciones = new VerticalLayout(cancelarButton, registrarButton);
        acciones.setWidthFull();
        acciones.getStyle().set("margin-top", "1.5rem");

        card.add(titulo, subtitulo, cedulaField, nombreField, cargoField, telefonoField, passwordField, acciones);
        add(card);
    }

    private void ejecutarRegistro() {
        if (cedulaField.isInvalid() || nombreField.isInvalid() || telefonoField.isInvalid()
                || cedulaField.isEmpty() || nombreField.isEmpty() || telefonoField.isEmpty()
                || cargoField.isEmpty() || passwordField.isEmpty()) {
            UiUtils.showError("[AVISO] Por favor, complete todos los campos respetando los formatos exigidos.");
            return;
        }

        try {
            Sistema sistema = new Sistema();
            // Constructor correcto con los 5 parámetros requeridos
            Usuario nuevo = new Usuario(
                    cedulaField.getValue().trim(),
                    nombreField.getValue().trim(),
                    cargoField.getValue(),
                    telefonoField.getValue().trim(),
                    passwordField.getValue()
            );

            sistema.gestorUsuario.registrarUsuario(nuevo);
            UiUtils.showSuccess("¡Registro exitoso! Ya puede iniciar sesión con sus credenciales.");
            UI.getCurrent().navigate("");
        } catch (Exception ex) {
            UiUtils.showError("Error al registrar usuario: " + ex.getMessage());
        }
    }
}
