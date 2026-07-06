package com.gestion.GUI;

import com.gestion.Interfaz.Sistema;
import com.gestion.Modelo.Falla;
import com.gestion.Modelo.Mantenimiento;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Route(value = "reportes", layout = MainLayout.class)
@PageTitle("Reportes")
public class ReportesView extends BaseSecuredView {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Solo el Administrador puede exportar los reportes del sistema
    @Override
    protected List<String> getRolesPermitidos() {
        return List.of(UiUtils.ROL_ADMINISTRADOR);
    }

    private final Tab tabFallas = new Tab("Reporte de Fallas");
    private final Tab tabMantenimientos = new Tab("Historial de Mantenimientos");
    private final Tabs tabs = new Tabs(tabFallas, tabMantenimientos);

    private final DatePicker desdeField = new DatePicker("Desde");
    private final DatePicker hastaField = new DatePicker("Hasta");

    private final H3 tituloResultado = new H3();
    private final Paragraph periodoResultado = new Paragraph();
    private final Paragraph totalResultado = new Paragraph();

    private final Grid<Falla> gridFallas = new Grid<>(Falla.class, false);
    private final Grid<Mantenimiento> gridMantenimientos = new Grid<>(Mantenimiento.class, false);

    private final Anchor descargarPdf = new Anchor();
    private final Button btnPdf = new Button("Exportar PDF", VaadinIcon.FILE_TEXT.create());

    public ReportesView() {
        setSizeFull();
        getStyle().set("background-color", UiUtils.COLOR_FONDO);
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.STRETCH);

        H2 titulo = new H2("Reportes");
        titulo.getStyle().set("margin", "0");
        titulo.getStyle().set("color", UiUtils.COLOR_TEXTO);

        HorizontalLayout contenedor = new HorizontalLayout();
        contenedor.setWidthFull();
        contenedor.setSpacing(true);
        contenedor.setAlignItems(FlexComponent.Alignment.STRETCH);

        VerticalLayout panelFiltros = construirPanelFiltros();
        VerticalLayout panelResultado = construirPanelResultado();

        contenedor.add(panelFiltros, panelResultado);
        contenedor.setFlexGrow(0, panelFiltros);
        contenedor.setFlexGrow(1, panelResultado);

        tabs.addSelectedChangeListener(event -> generarReporte());

        add(titulo, contenedor);
        expand(contenedor);

        generarReporte();
    }

    private VerticalLayout construirPanelFiltros() {
        VerticalLayout panel = UiUtils.createCard();
        panel.setWidth("300px");
        panel.setSpacing(true);
        panel.setPadding(true);

        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.setWidthFull();

        H3 tituloFiltros = new H3("Filtros");
        tituloFiltros.getStyle().set("margin", "0.5rem 0 0 0");
        tituloFiltros.getStyle().set("color", UiUtils.COLOR_TEXTO);

        desdeField.setWidthFull();
        desdeField.setValue(LocalDate.now().minusMonths(1));

        hastaField.setWidthFull();
        hastaField.setValue(LocalDate.now());

        Button btnGenerar = new Button("Generar Reporte");
        UiUtils.stylePrimary(btnGenerar);
        btnGenerar.setWidthFull();
        btnGenerar.addClickListener(e -> generarReporte());

        panel.add(tabs, tituloFiltros, desdeField, hastaField, btnGenerar);
        return panel;
    }

    private VerticalLayout construirPanelResultado() {
        VerticalLayout panel = UiUtils.createCard();
        panel.setWidthFull();
        panel.setSpacing(true);
        panel.setPadding(true);

        tituloResultado.getStyle().set("margin", "0");
        tituloResultado.getStyle().set("color", UiUtils.COLOR_TEXTO);

        periodoResultado.getStyle().set("margin", "0");
        periodoResultado.getStyle().set("color", "#6B7280");

        totalResultado.getStyle().set("margin", "0 0 0.5rem 0");
        totalResultado.getStyle().set("font-weight", "700");
        totalResultado.getStyle().set("color", UiUtils.COLOR_TEXTO);

        configurarGridFallas();
        configurarGridMantenimientos();
        gridMantenimientos.setVisible(false);

        UiUtils.stylePrimary(btnPdf);
        descargarPdf.add(btnPdf);

        HorizontalLayout acciones = new HorizontalLayout(descargarPdf);
        acciones.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        acciones.setWidthFull();

        panel.add(tituloResultado, periodoResultado, totalResultado, gridFallas, gridMantenimientos, acciones);
        panel.expand(gridFallas);
        return panel;
    }

    private void configurarGridFallas() {
        gridFallas.removeAllColumns();
        gridFallas.addColumn(Falla::getId).setHeader("ID Falla").setAutoWidth(true).setFlexGrow(0);
        gridFallas.addColumn(Falla::getIdEquipo).setHeader("ID Equipo").setAutoWidth(true).setFlexGrow(0);
        gridFallas.addColumn(Falla::getDescripcion).setHeader("Descripción").setAutoWidth(true);
        gridFallas.addColumn(f -> FORMATO_FECHA.format(f.getFechaReporte())).setHeader("Fecha").setAutoWidth(true).setFlexGrow(0);
        gridFallas.addColumn(Falla::getUsuarioReporta).setHeader("Reportado por").setAutoWidth(true);
        gridFallas.addColumn(new ComponentRenderer<>(f -> UiUtils.createBadge(f.getEstadoFalla()))).setHeader("Estado").setAutoWidth(true);
        gridFallas.setHeight("calc(100vh - 380px)");
    }

    private void configurarGridMantenimientos() {
        gridMantenimientos.removeAllColumns();
        gridMantenimientos.addColumn(Mantenimiento::getId).setHeader("ID Mantenimiento").setAutoWidth(true).setFlexGrow(0);
        gridMantenimientos.addColumn(Mantenimiento::getIdEquipo).setHeader("ID Equipo").setAutoWidth(true).setFlexGrow(0);
        gridMantenimientos.addColumn(Mantenimiento::getTipo).setHeader("Tipo").setAutoWidth(true).setFlexGrow(0);
        gridMantenimientos.addColumn(Mantenimiento::getDescripcion).setHeader("Descripción").setAutoWidth(true);
        gridMantenimientos.addColumn(m -> FORMATO_FECHA.format(m.getFecha())).setHeader("Fecha").setAutoWidth(true).setFlexGrow(0);
        gridMantenimientos.addColumn(Mantenimiento::getTecnicoResponsable).setHeader("Técnico Responsable").setAutoWidth(true);
        gridMantenimientos.setHeight("calc(100vh - 380px)");
    }

    private boolean esReporteFallas() {
        return tabs.getSelectedTab() == tabFallas;
    }

    private void generarReporte() {
        LocalDate desde = desdeField.getValue();
        LocalDate hasta = hastaField.getValue();

        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            Notification.show("La fecha 'Desde' no puede ser posterior a la fecha 'Hasta'.", 3500, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        String periodoTexto = "Periodo: " + (desde != null ? FORMATO_FECHA.format(desde) : "—")
                + " - " + (hasta != null ? FORMATO_FECHA.format(hasta) : "—");
        periodoResultado.setText(periodoTexto);

        boolean esFallas = esReporteFallas();
        gridFallas.setVisible(esFallas);
        gridMantenimientos.setVisible(!esFallas);

        if (esFallas) {
            tituloResultado.setText("Reporte de Fallas");
            List<Falla> filtradas = Sistema.gestorFallas.getLista().stream()
                    .filter(f -> f.getFechaReporte() != null
                            && (desde == null || !f.getFechaReporte().isBefore(desde))
                            && (hasta == null || !f.getFechaReporte().isAfter(hasta)))
                    .toList();
            gridFallas.setItems(filtradas);
            totalResultado.setText("Total fallas: " + filtradas.size());
            actualizarDescargaPdf("Reporte de Fallas", periodoTexto, "Total fallas: " + filtradas.size(),
                    new String[]{"ID Falla", "ID Equipo", "Descripción", "Fecha", "Reportado por", "Estado"},
                    filtradas.stream().map(f -> new String[]{
                            f.getId(), f.getIdEquipo(), f.getDescripcion(),
                            FORMATO_FECHA.format(f.getFechaReporte()), f.getUsuarioReporta(), f.getEstadoFalla()
                    }).toList(),
                    "Reporte_Fallas.pdf");
        } else {
            tituloResultado.setText("Historial de Mantenimientos");
            List<Mantenimiento> filtrados = Sistema.gestorMant.getLista().stream()
                    .filter(m -> m.getFecha() != null
                            && (desde == null || !m.getFecha().isBefore(desde))
                            && (hasta == null || !m.getFecha().isAfter(hasta)))
                    .toList();
            gridMantenimientos.setItems(filtrados);
            totalResultado.setText("Total mantenimientos: " + filtrados.size());
            actualizarDescargaPdf("Historial de Mantenimientos", periodoTexto, "Total mantenimientos: " + filtrados.size(),
                    new String[]{"ID Mantenimiento", "ID Equipo", "Tipo", "Descripción", "Fecha", "Técnico Responsable"},
                    filtrados.stream().map(m -> new String[]{
                            m.getId(), m.getIdEquipo(), m.getTipo(), m.getDescripcion(),
                            FORMATO_FECHA.format(m.getFecha()), m.getTecnicoResponsable()
                    }).toList(),
                    "Historial_Mantenimientos.pdf");
        }
    }

    private void actualizarDescargaPdf(String titulo, String periodo, String total, String[] encabezados, List<String[]> filas, String nombreArchivo) {
        byte[] pdfBytes = generarPdf(titulo, periodo, total, encabezados, filas);
        String base64 = Base64.getEncoder().encodeToString(pdfBytes);
        descargarPdf.setHref("data:application/pdf;base64," + base64);
        descargarPdf.getElement().setAttribute("download", nombreArchivo);
    }

    // ---- GENERACIÓN DE PDF (sin dependencias externas) ----

    private byte[] generarPdf(String titulo, String periodo, String totalLinea, String[] encabezados, List<String[]> filas) {
        List<String> lineas = new ArrayList<>();
        lineas.add(titulo);
        lineas.add(periodo);
        lineas.add(totalLinea);
        lineas.add("");
        lineas.add(String.join("   |   ", encabezados));
        lineas.add("------------------------------------------------------------------------------");

        int maxFilas = 45; // límite razonable para una sola página tamaño carta
        int mostrar = Math.min(filas.size(), maxFilas);
        for (int i = 0; i < mostrar; i++) {
            lineas.add(String.join("   |   ", filas.get(i)));
        }
        if (filas.size() > maxFilas) {
            lineas.add("");
            lineas.add("(Mostrando las primeras " + maxFilas + " de " + filas.size() + " filas)");
        }

        return construirPdfDesdeTexto(lineas);
    }

    private byte[] construirPdfDesdeTexto(List<String> lineas) {
        StringBuilder streamTexto = new StringBuilder();
        streamTexto.append("BT\n/F1 11 Tf\n14 TL\n40 760 Td\n");
        boolean primero = true;
        for (String linea : lineas) {
            String segura = escaparTextoPdf(linea);
            if (primero) {
                streamTexto.append("(").append(segura).append(") Tj\n");
                primero = false;
            } else {
                streamTexto.append("T*\n(").append(segura).append(") Tj\n");
            }
        }
        streamTexto.append("ET");

        Charset cs = StandardCharsets.ISO_8859_1;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            List<Integer> offsets = new ArrayList<>();

            out.write("%PDF-1.4\n".getBytes(cs));

            offsets.add(out.size());
            out.write("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n".getBytes(cs));

            offsets.add(out.size());
            out.write("2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n".getBytes(cs));

            offsets.add(out.size());
            out.write("3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Resources << /Font << /F1 5 0 R >> >> /Contents 4 0 R >>\nendobj\n".getBytes(cs));

            offsets.add(out.size());
            byte[] contenidoBytes = streamTexto.toString().getBytes(cs);
            out.write(("4 0 obj\n<< /Length " + contenidoBytes.length + " >>\nstream\n").getBytes(cs));
            out.write(contenidoBytes);
            out.write("\nendstream\nendobj\n".getBytes(cs));

            offsets.add(out.size());
            out.write("5 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n".getBytes(cs));

            int xrefStart = out.size();
            out.write(("xref\n0 " + (offsets.size() + 1) + "\n").getBytes(cs));
            out.write("0000000000 65535 f \n".getBytes(cs));
            for (int offset : offsets) {
                out.write(String.format("%010d 00000 n \n", offset).getBytes(cs));
            }
            out.write(("trailer\n<< /Size " + (offsets.size() + 1) + " /Root 1 0 R >>\nstartxref\n" + xrefStart + "\n%%EOF").getBytes(cs));

            return out.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }

    private String escaparTextoPdf(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }
}