package progra4.prestamos.service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import progra4.prestamos.model.Puesto;
import progra4.prestamos.repository.PuestoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final PuestoRepository puestoRepo;

    public byte[] puestosPorMesPdf(int anio, int mes) throws Exception {
        LocalDateTime inicio = LocalDate.of(anio, mes, 1).atStartOfDay();
        LocalDateTime fin    = inicio.plusMonths(1).minusSeconds(1);

        List<Puesto> puestos = puestoRepo.findByRangoDeFechas(inicio, fin);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 40, 40, 50, 40);
        PdfWriter.getInstance(doc, baos);
        doc.open();

        // Fuentes
        Font fTitulo  = new Font(Font.HELVETICA, 16, Font.BOLD,   new Color(26, 31, 46));
        Font fSub     = new Font(Font.HELVETICA, 11, Font.NORMAL, new Color(100, 100, 100));
        Font fHeader  = new Font(Font.HELVETICA, 10, Font.BOLD,   Color.WHITE);
        Font fCell    = new Font(Font.HELVETICA,  9, Font.NORMAL, new Color(40, 40, 40));
        Font fEmpty   = new Font(Font.HELVETICA, 10, Font.ITALIC, new Color(120, 120, 120));

        // Título
        Paragraph titulo = new Paragraph("Reporte de Puestos", fTitulo);
        titulo.setSpacingAfter(4);
        doc.add(titulo);

        String nombreMes = nombreMes(mes);
        Paragraph sub = new Paragraph(
                "Período: " + nombreMes + " " + anio +
                        "   |   Total: " + puestos.size() + " puesto(s)", fSub);
        sub.setSpacingAfter(16);
        doc.add(sub);

        // Tabla
        PdfPTable tabla = new PdfPTable(6);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{5, 28, 20, 14, 10, 14});

        String[] headers = {"ID", "Descripción", "Empresa", "Salario", "Público", "Fecha"};
        Color colorHeader = new Color(26, 31, 46);
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, fHeader));
            cell.setBackgroundColor(colorHeader);
            cell.setPadding(7);
            cell.setBorder(Rectangle.NO_BORDER);
            tabla.addCell(cell);
        }

        Color colorPar   = new Color(249, 250, 251);
        Color colorImpar = Color.WHITE;

        if (puestos.isEmpty()) {
            PdfPCell vacio = new PdfPCell(new Phrase("No hay puestos registrados en este período.", fEmpty));
            vacio.setColspan(6);
            vacio.setHorizontalAlignment(Element.ALIGN_CENTER);
            vacio.setPadding(14);
            vacio.setBorder(Rectangle.BOX);
            tabla.addCell(vacio);
        } else {
            int fila = 0;
            for (Puesto p : puestos) {
                Color bg = (fila % 2 == 0) ? colorImpar : colorPar;
                String[] vals = {
                        String.valueOf(p.getId()),
                        p.getDescripcion(),
                        p.getEmpresa().getNombre(),
                        p.getSalario().toPlainString(),
                        p.getEsPublico() ? "Sí" : "No",
                        p.getFechaRegistro().toLocalDate().toString()
                };
                for (String v : vals) {
                    PdfPCell cell = new PdfPCell(new Phrase(v, fCell));
                    cell.setBackgroundColor(bg);
                    cell.setPadding(6);
                    cell.setBorderColor(new Color(229, 231, 235));
                    cell.setBorderWidth(0.5f);
                    tabla.addCell(cell);
                }
                fila++;
            }
        }
        doc.add(tabla);
        doc.close();
        return baos.toByteArray();
    }

    private String nombreMes(int mes) {
        return switch (mes) {
            case 1  -> "Enero";      case 2  -> "Febrero";
            case 3  -> "Marzo";      case 4  -> "Abril";
            case 5  -> "Mayo";       case 6  -> "Junio";
            case 7  -> "Julio";      case 8  -> "Agosto";
            case 9  -> "Septiembre"; case 10 -> "Octubre";
            case 11 -> "Noviembre";  case 12 -> "Diciembre";
            default -> "Mes " + mes;
        };
    }
}
