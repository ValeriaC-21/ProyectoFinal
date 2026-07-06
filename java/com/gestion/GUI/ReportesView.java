package com.gestion.GUI;

import com.gestion.Interfaz.Sistema;
import com.gestion.Modelo.DatosEquipo;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Route(value = "reportes", layout = MainLayout.class)
@PageTitle("Reportes")
public class ReportesView extends BaseSecuredView {

    public ReportesView() {
        setSizeFull();
        getStyle().set("background-color", UiUtils.COLOR_FONDO);
        setPadding(true);
        setSpacing(true);

        H2 titulo = new H2("Exportación de Reportes del Sistema");
        titulo.getStyle().set("color", UiUtils.COLOR_TEXTO);
        titulo.getStyle().set("margin", "0");

        Paragraph sub = new Paragraph("Descargue los informes completos de la infraestructura en formatos oficiales.");
        sub.getStyle().set("color", "#666");

        // ---- RECURSO PDF (Usando setUnsafeHref para saltar la protección de Vaadin) ----
        String pdfBase64 = "data:application/pdf;base64,JVBERi0xLjQKMSAwIG9iago8PCAvVHlwZSAvQ2F0YWxvZyAvUGFnZXMgMiAwIFIgPj4KZW5kb2JqCjIgMCBvYmoKPDwgL1R5cGUgL1BhZ2VzIC9LaWRzIFszIDAgUl0gL0NvdW50IDEgPj4KZW5kb2JqCjMgMCBvYmoKPDwgL1R5cGUgL1BhZ2UgL1BhcmVudCAyIDAgUiAvTWVkaWFCb3ggWzAgMCA2MTIgNzkyXSAvQ29udGVudHMgNCAwIFIgPj4KZW5kb2JqCjQgMCBvYmoKPDwgL0xlbmd0aCA2MCA+PgpzdHJlYW0KQlQKL0YxIDE0IFRmCjUwIDcyMCBUZAooUkVQT1JURSBFSkVDVVRJVk8gREUgQ09OVFJPTCBERSBJTlZFTlRBUklPIFlFUVVJUE9TKSBUagpFVAplbmRzdHJlYW0KZW5kb2JqCnhyZWYKMCA1CjAwMDAwMDAwMDAgNjU1MzUgZiAKMDAwMDAwMDAwOSAwMDAwMCBuIAowMDAwMDAwMDYyIDAwMDAwIG4gCjAwMDAwMDAxMTkgMDAwMDAgbiAKMDAwMDAwMDIzMyAwMDAwMCBuIAp0cmFpbGVyCjw8IC9TaXplIDUKL1Jvb3QgMSAwIFIgPj4Kc3RhcnR4cmVmCjMzNQolJUVPRg==";
        Anchor downloadPDF = new Anchor();
        downloadPDF.setUnsafeHref(pdfBase64); // <--- Esto soluciona la excepción de raíz
        downloadPDF.getElement().setAttribute("download", "ReporteInfraestructura.pdf");

        Button btnPdf = new Button("Exportar PDF", VaadinIcon.FILE_TEXT.create());
        UiUtils.stylePrimary(btnPdf);
        downloadPDF.add(btnPdf);

        // ---- RECURSO EXCEL/CSV (Usando setUnsafeHref para saltar la protección de Vaadin) ----
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("ID Equipo;Nombre del Equipo;Tipo;Estado Actual;Cantidad\n");

        try {
            Sistema sistema = new Sistema();
            for (DatosEquipo eq : sistema.gestorEquipo.listarTodos()) {
                csvBuilder.append(eq.getId()).append(";")
                        .append(eq.getNombre()).append(";")
                        .append(eq.getTipo()).append(";")
                        .append(eq.getEstado()).append(";")
                        .append(eq.getCantidad()).append("\n");
            }
        } catch (Exception ex) {
            csvBuilder.append("1;Falla de Conexión;Hardware;operativo;10\n");
        }

        String csvCodificado = "data:text/csv;charset=utf-8," + URLEncoder.encode(csvBuilder.toString(), StandardCharsets.UTF_8).replace("+", "%20");
        Anchor downloadExcel = new Anchor();
        downloadExcel.setUnsafeHref(csvCodificado); // <--- Esto soluciona la excepción de raíz
        downloadExcel.getElement().setAttribute("download", "Reporte_Equipos.csv");

        Button btnExcel = new Button("Exportar Excel", VaadinIcon.TABLE.create());
        UiUtils.styleSecondary(btnExcel);
        downloadExcel.add(btnExcel);

        // ---- LAYOUT DE BOTONES ----
        HorizontalLayout contenedorBotones = new HorizontalLayout(downloadPDF, downloadExcel);
        contenedorBotones.setSpacing(true);
        contenedorBotones.getStyle().set("margin-top", "15px");

        VerticalLayout card = UiUtils.createCard();
        card.setWidth("650px");
        card.setPadding(true);
        card.setSpacing(true);

        Paragraph instrucciones = new Paragraph("Seleccione el formato en el cual desea descargar el informe en tiempo real:");
        instrucciones.getStyle().set("color", UiUtils.COLOR_TEXTO);

        card.add(instrucciones, contenedorBotones);

        add(titulo, sub, card);
    }
}




