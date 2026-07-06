package com.gestion.GUI;
import com.gestion.Interfaz.Sistema;
import com.gestion.Modelo.DatosEquipo;
import com.gestion.Modelo.Falla;
import com.gestion.Modelo.Mantenimiento;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import java.util.List;
@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard")
public class DashboardView extends BaseSecuredView {
    private final HorizontalLayout kpiContainer = new HorizontalLayout();
    private final Grid<Mantenimiento> gridMantenimientos = new Grid<>(Mantenimiento.class, false);
    private final Grid<Falla> gridFallas = new Grid<>(Falla.class, false);
    public DashboardView() {
        setSizeFull();
        getStyle().set("background-color", UiUtils.COLOR_FONDO);
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.STRETCH);
        H2 titulo = new H2("Panel principal");
        titulo.getStyle().set("margin", "0");
        titulo.getStyle().set("color", UiUtils.COLOR_TEXTO);
        Paragraph subtitulo = new Paragraph("Vista general del estado actual de equipos, mantenimientos y fallas.");
        subtitulo.getStyle().set("margin", "0");
        subtitulo.getStyle().set("color", "#4B5563");
        kpiContainer.setWidthFull();
        kpiContainer.setSpacing(true);
        kpiContainer.setPadding(false);
        kpiContainer.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.STRETCH);
        configurarGridMantenimientos();
        configurarGridFallas();
        HorizontalLayout tablas = new HorizontalLayout();
        tablas.setWidthFull();
        tablas.setSpacing(true);
        tablas.setPadding(false);
        tablas.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.STRETCH);
        VerticalLayout cardMantenimiento = crearTarjetaTabla("Últimos Mantenimientos", gridMantenimientos);
        VerticalLayout cardFallas = crearTarjetaTabla("Últimas Fallas", gridFallas);
        tablas.add(cardMantenimiento, cardFallas);
        tablas.expand(cardMantenimiento, cardFallas);

        // El Operador no debe visualizar las tablas de Últimos Mantenimientos ni Últimas Fallas
        boolean esOperador = UiUtils.tieneRol(getUsuarioActivo(), UiUtils.ROL_OPERADOR);
        tablas.setVisible(!esOperador);

        add(titulo, subtitulo, kpiContainer, tablas);
        if (!esOperador) {
            expand(tablas);
        }
    }
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        refrescarContenido();
    }
    private void refrescarContenido() {
        kpiContainer.removeAll();
        kpiContainer.add(
                crearTarjetaKpi("Equipos Registrados", String.valueOf(Sistema.gestorEquipo.listarTodos().size()), VaadinIcon.DESKTOP),
                crearTarjetaKpi("Operativos", String.valueOf(contarPorEstado("operativo")), VaadinIcon.CHECK),
                crearTarjetaKpi("En Mantenimiento", String.valueOf(contarPorEstado("en mantenimiento")), VaadinIcon.WRENCH),
                crearTarjetaKpi("Fuera de Servicio", String.valueOf(contarPorEstado("fuera de servicio")), VaadinIcon.EXCLAMATION_CIRCLE)
        );
        gridMantenimientos.setItems(Sistema.gestorMant.getLista());
        gridFallas.setItems(Sistema.gestorFallas.getLista());
    }
    private long contarPorEstado(String estado) {
        List<DatosEquipo> equipos = Sistema.gestorEquipo.listarTodos();
        return equipos.stream().filter(equipo -> estado.equalsIgnoreCase(equipo.getEstado())).count();
    }
    private VerticalLayout crearTarjetaKpi(String titulo, String valor, VaadinIcon icono) {
        VerticalLayout card = UiUtils.createCard();
        card.setWidthFull();
        card.setSpacing(true);
        card.setPadding(true);
        card.getStyle().set("min-height", "150px");
        card.setAlignItems(Alignment.START);
        HorizontalLayout linea = new HorizontalLayout();
        linea.setWidthFull();
        linea.setAlignItems(FlexComponent.Alignment.CENTER);
        linea.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        H3 label = new H3(titulo);
        label.getStyle().set("margin", "0");
        label.getStyle().set("color", UiUtils.COLOR_TEXTO);
        label.getStyle().set("font-size", "1rem");
        Icon icon = icono.create();
        icon.setSize("26px");
        icon.getStyle().set("color", UiUtils.COLOR_PRINCIPAL);
        linea.add(label, icon);
        Span numero = new Span(valor);
        numero.getStyle().set("font-size", "2.3rem");
        numero.getStyle().set("font-weight", "800");
        numero.getStyle().set("color", UiUtils.COLOR_TEXTO);
        card.add(linea, numero);
        return card;
    }
    private VerticalLayout crearTarjetaTabla(String titulo, Component tabla) {
        VerticalLayout card = UiUtils.createCard();
        card.setWidthFull();
        card.setSpacing(true);
        card.setPadding(true);
        card.setAlignItems(Alignment.STRETCH);
        card.getStyle().set("min-height", "420px");
        H3 label = new H3(titulo);
        label.getStyle().set("margin", "0");
        label.getStyle().set("color", UiUtils.COLOR_TEXTO);
        card.add(label, tabla);
        card.expand(tabla);
        return card;
    }
    private void configurarGridMantenimientos() {
        gridMantenimientos.removeAllColumns();
        gridMantenimientos.addColumn(Mantenimiento::getId).setHeader("ID").setAutoWidth(true).setFlexGrow(0);
        gridMantenimientos.addColumn(Mantenimiento::getIdEquipo).setHeader("Equipo").setAutoWidth(true).setFlexGrow(0);
        gridMantenimientos.addColumn(Mantenimiento::getTecnicoResponsable).setHeader("Técnico Responsable").setAutoWidth(true);
        gridMantenimientos.addColumn(Mantenimiento::getFecha).setHeader("Fecha").setAutoWidth(true).setFlexGrow(0);
        gridMantenimientos.setHeight("320px");
    }
    private void configurarGridFallas() {
        gridFallas.removeAllColumns();
        gridFallas.addColumn(Falla::getId).setHeader("ID").setAutoWidth(true).setFlexGrow(0);
        gridFallas.addColumn(Falla::getIdEquipo).setHeader("ID Equipo").setAutoWidth(true).setFlexGrow(0);
        gridFallas.addColumn(new ComponentRenderer<>(falla -> UiUtils.createBadge(falla.getEstadoFalla()))).setHeader("Estado").setAutoWidth(true);
        gridFallas.addColumn(Falla::getFechaReporte).setHeader("Fecha").setAutoWidth(true).setFlexGrow(0);
        gridFallas.setHeight("320px");
    }
}
