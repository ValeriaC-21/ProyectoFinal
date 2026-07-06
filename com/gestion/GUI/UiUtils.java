package com.gestion.GUI;

import com.gestion.Modelo.Usuario;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.VaadinSession;

public final class UiUtils {

    public static final String COLOR_PRINCIPAL = "#2563EB";
    public static final String COLOR_EXITO = "#22C55E";
    public static final String COLOR_ADVERTENCIA = "#F59E0B";
    public static final String COLOR_ERROR = "#EF4444";
    public static final String COLOR_FONDO = "#F4F6F8";
    public static final String COLOR_TEXTO = "#1F2937";

    // ---- ROLES / CARGOS (deben coincidir exactamente con los usados en GestorUsuario) ----
    public static final String ROL_ADMINISTRADOR = "Administrador";
    public static final String ROL_TECNICO = "Técnico de Mantenimiento";
    public static final String ROL_OPERADOR = "Operador";

    private UiUtils() {
    }

    /** Verifica si el usuario dado tiene alguno de los cargos indicados (sin distinguir mayúsculas/minúsculas). */
    public static boolean tieneRol(Usuario usuario, String... roles) {
        if (usuario == null || roles == null) {
            return false;
        }
        for (String rol : roles) {
            if (rol != null && rol.equalsIgnoreCase(usuario.getCargo())) {
                return true;
            }
        }
        return false;
    }

    public static void stylePrimary(Button button) {
        button.getElement().getStyle().set("background-color", COLOR_PRINCIPAL);
        button.getElement().getStyle().set("color", "white");
        button.getElement().getStyle().set("border", "none");
        button.getElement().getStyle().set("border-radius", "12px");
        button.getElement().getStyle().set("font-weight", "600");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }

    public static void styleSecondary(Button button) {
        button.getElement().getStyle().set("background-color", "white");
        button.getElement().getStyle().set("color", COLOR_TEXTO);
        button.getElement().getStyle().set("border", "1px solid #D1D5DB");
        button.getElement().getStyle().set("border-radius", "12px");
        button.getElement().getStyle().set("font-weight", "600");
    }

    public static void styleDanger(Button button) {
        button.getElement().getStyle().set("background-color", COLOR_ERROR);
        button.getElement().getStyle().set("color", "white");
        button.getElement().getStyle().set("border", "none");
        button.getElement().getStyle().set("border-radius", "12px");
        button.getElement().getStyle().set("font-weight", "600");
    }

    public static void styleSuccess(Button button) {
        button.getElement().getStyle().set("background-color", COLOR_EXITO);
        button.getElement().getStyle().set("color", "white");
        button.getElement().getStyle().set("border", "none");
        button.getElement().getStyle().set("border-radius", "12px");
        button.getElement().getStyle().set("font-weight", "600");
    }

    public static void styleGhost(Button button) {
        button.getElement().getStyle().set("background-color", "transparent");
        button.getElement().getStyle().set("border", "none");
        button.getElement().getStyle().set("box-shadow", "none");
        button.getElement().getStyle().set("color", COLOR_TEXTO);
    }

    public static Span createBadge(String estado) {
        String normalized = estado == null ? "" : estado.trim().toLowerCase();
        String color = switch (normalized) {
            case "operativo" -> COLOR_EXITO;
            case "en mantenimiento" -> COLOR_ADVERTENCIA;
            case "fuera de servicio" -> COLOR_ERROR;
            case "pendiente" -> COLOR_ADVERTENCIA;
            case "en revisión" -> COLOR_PRINCIPAL;
            case "resuelta" -> COLOR_EXITO;
            case "disponible" -> COLOR_EXITO;
            case "agotado" -> COLOR_ERROR;
            default -> "#6B7280";
        };
        Span badge = new Span(estado == null || estado.isBlank() ? "sin estado" : estado);
        badge.getElement().getStyle().set("background-color", toPastel(color));
        badge.getElement().getStyle().set("color", color);
        badge.getElement().getStyle().set("padding", "0.35rem 0.7rem");
        badge.getElement().getStyle().set("border-radius", "999px");
        badge.getElement().getStyle().set("font-size", "0.78rem");
        badge.getElement().getStyle().set("font-weight", "700");
        badge.getElement().getStyle().set("text-transform", "none");
        return badge;
    }

    public static com.vaadin.flow.component.orderedlayout.VerticalLayout createCard() {
        com.vaadin.flow.component.orderedlayout.VerticalLayout card =
                new com.vaadin.flow.component.orderedlayout.VerticalLayout();
        card.getStyle().set("background", "white");
        card.getStyle().set("border-radius", "18px");
        card.getStyle().set("box-shadow", "0 10px 28px rgba(15,23,42,0.08)");
        card.getStyle().set("padding", "1.5rem");
        card.setSpacing(true);
        card.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.STRETCH);
        return card;
    }

    public static Button iconButton(VaadinIcon icon, String color, Runnable action) {
        Button button = new Button(icon.create(), event -> {
            if (action != null) {
                action.run();
            }
        });
        button.getElement().getStyle().set("background", "transparent");
        button.getElement().getStyle().set("border", "none");
        button.getElement().getStyle().set("color", color);
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        button.getElement().getStyle().set("padding", "0.15rem");
        return button;
    }

    public static void showError(String message) {
        com.vaadin.flow.component.notification.Notification notification =
                com.vaadin.flow.component.notification.Notification.show(message, 4500,
                        com.vaadin.flow.component.notification.Notification.Position.MIDDLE);
        notification.addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_ERROR);
    }

    public static void showSuccess(String message) {
        com.vaadin.flow.component.notification.Notification notification =
                com.vaadin.flow.component.notification.Notification.show(message, 3500,
                        com.vaadin.flow.component.notification.Notification.Position.MIDDLE);
        notification.addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_SUCCESS);
    }

    public static String toPastel(String colorHex) {
        String hex = colorHex == null ? "#6B7280" : colorHex.trim();
        if (!hex.startsWith("#") || hex.length() != 7) {
            return "rgba(107, 114, 128, 0.10)";
        }
        int red = Integer.parseInt(hex.substring(1, 3), 16);
        int green = Integer.parseInt(hex.substring(3, 5), 16);
        int blue = Integer.parseInt(hex.substring(5, 7), 16);
        return "rgba(" + red + ", " + green + ", " + blue + ", 0.10)";
    }

    public static Usuario getUsuarioActivo() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) {
            return null;
        }
        return session.getAttribute(Usuario.class);
    }

    public static void setUsuarioActivo(Usuario usuario) {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute(Usuario.class, usuario);
        }
    }

    public static void limpiarSesion() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute(Usuario.class, null);
        }
    }

    public static String nombreConCargo(Usuario usuario) {
        if (usuario == null) {
            return "Sin sesión";
        }
        return usuario.getNombre() + " (" + usuario.getCargo() + ")";
    }
}