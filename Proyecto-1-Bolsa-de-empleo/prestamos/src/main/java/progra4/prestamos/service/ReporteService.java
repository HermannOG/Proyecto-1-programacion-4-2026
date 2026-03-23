package progra4.prestamos.service;

import progra4.prestamos.model.Puesto;
import progra4.prestamos.repository.PuestoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final PuestoRepository puestoRepo;

    /**
     * Genera un String HTML con el reporte de puestos por mes.
     * El navegador lo puede imprimir como PDF con Ctrl+P / Guardar como PDF.
     */
    public String puestosPorMesHtml(int anio, int mes) {
        LocalDateTime inicio = LocalDate.of(anio, mes, 1).atStartOfDay();
        LocalDateTime fin    = inicio.plusMonths(1).minusSeconds(1);

        List<Puesto> puestos = puestoRepo.findByRangoDeFechas(inicio, fin);

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        sb.append("<title>Reporte de Puestos</title>");
        sb.append("<style>");
        sb.append("body{font-family:Arial,sans-serif;padding:32px;}");
        sb.append("h1{font-size:22px;margin-bottom:4px;}");
        sb.append("p{font-size:14px;color:#555;margin-bottom:20px;}");
        sb.append("table{width:100%;border-collapse:collapse;font-size:13px;}");
        sb.append("th{background:#1a1f2e;color:white;padding:10px 12px;text-align:left;}");
        sb.append("td{padding:9px 12px;border-bottom:1px solid #e5e7eb;}");
        sb.append("tr:nth-child(even) td{background:#f9fafb;}");
        sb.append(".btn{margin-bottom:20px;padding:8px 18px;background:#2563eb;color:white;");
        sb.append("border:none;border-radius:5px;cursor:pointer;font-size:13px;}");
        sb.append("@media print{.btn{display:none;}}");
        sb.append("</style></head><body>");

        sb.append("<h1>Reporte de Puestos</h1>");
        sb.append("<p>Per&iacute;odo: ").append(nombreMes(mes)).append(" ").append(anio)
                .append(" &nbsp;|&nbsp; Total: ").append(puestos.size()).append(" puesto(s)</p>");

        sb.append("<button class='btn' onclick='window.print()'>")
                .append("Imprimir / Guardar PDF</button>");

        sb.append("<table><tr>")
                .append("<th>ID</th><th>Descripci&oacute;n</th><th>Empresa</th>")
                .append("<th>Salario</th><th>P&uacute;blico</th><th>Fecha</th>")
                .append("</tr>");

        for (Puesto p : puestos) {
            sb.append("<tr>")
                    .append("<td>").append(p.getId()).append("</td>")
                    .append("<td>").append(p.getDescripcion()).append("</td>")
                    .append("<td>").append(p.getEmpresa().getNombre()).append("</td>")
                    .append("<td>").append(p.getSalario()).append("</td>")
                    .append("<td>").append(p.getEsPublico() ? "S&iacute;" : "No").append("</td>")
                    .append("<td>").append(p.getFechaRegistro().toLocalDate()).append("</td>")
                    .append("</tr>");
        }

        if (puestos.isEmpty()) {
            sb.append("<tr><td colspan='6' style='text-align:center;color:#6b7280;padding:20px;'>")
                    .append("No hay puestos registrados en este per&iacute;odo.</td></tr>");
        }

        sb.append("</table></body></html>");
        return sb.toString();
    }

    private String nombreMes(int mes) {
        return switch (mes) {
            case 1  -> "Enero";    case 2  -> "Febrero";
            case 3  -> "Marzo";    case 4  -> "Abril";
            case 5  -> "Mayo";     case 6  -> "Junio";
            case 7  -> "Julio";    case 8  -> "Agosto";
            case 9  -> "Septiembre"; case 10 -> "Octubre";
            case 11 -> "Noviembre";  case 12 -> "Diciembre";
            default -> "Mes " + mes;
        };
    }
}