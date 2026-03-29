package progra4.prestamos.controller;

import progra4.prestamos.model.Caracteristica;
import progra4.prestamos.service.CaracteristicaService;
import progra4.prestamos.service.EmpresaService;
import progra4.prestamos.service.OferenteService;
import progra4.prestamos.service.ReporteService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final EmpresaService empresaService;
    private final OferenteService oferenteService;
    private final CaracteristicaService caracteristicaService;
    private final ReporteService reporteService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    // ---- Aprobaciones empresas ----
    @GetMapping("/empresas/pendientes")
    public String empresasPendientes(Model model) {
        model.addAttribute("empresas", empresaService.pendientes());
        return "admin/empresas-pendientes";
    }

    @PostMapping("/empresas/{id}/aprobar")
    public String aprobarEmpresa(@PathVariable Integer id) {
        empresaService.aprobar(id);
        return "redirect:/admin/empresas/pendientes";
    }

    // ---- Aprobaciones oferentes ----
    @GetMapping("/oferentes/pendientes")
    public String oferentesPendientes(Model model) {
        model.addAttribute("oferentes", oferenteService.pendientes());
        return "admin/oferentes-pendientes";
    }

    @PostMapping("/oferentes/{id}/aprobar")
    public String aprobarOferente(@PathVariable Integer id) {
        oferenteService.aprobar(id);
        return "redirect:/admin/oferentes/pendientes";
    }

    // ---- Características ----
    @GetMapping("/caracteristicas")
    public String caracteristicas(@RequestParam(required = false) Integer actualId,
                                  Model model) {
        model.addAttribute("raices", caracteristicaService.raices());
        model.addAttribute("nueva", new Caracteristica());
        if (actualId != null) {
            model.addAttribute("actual", caracteristicaService.obtener(actualId));
        }
        return "admin/caracteristicas";
    }

    @PostMapping("/caracteristicas")
    public String crearCaracteristica(@RequestParam String nombre,
                                      @RequestParam(required = false) Integer padreId,
                                      @RequestParam(required = false) Integer actualId) {
        Caracteristica c = new Caracteristica();
        c.setNombre(nombre);
        caracteristicaService.crear(c, padreId);
        if (actualId != null) {
            return "redirect:/admin/caracteristicas?actualId=" + actualId;
        }
        return "redirect:/admin/caracteristicas";
    }

    // ---- Reportes ----
    @GetMapping("/reportes")
    public String reporteForm() {
        return "admin/reportes";
    }

    // Genera reporte PDF real (OpenPDF) y lo envía como descarga
    @GetMapping("/reportes/pdf")
    public void generarReporte(@RequestParam int anio,
                               @RequestParam int mes,
                               HttpServletResponse response) throws Exception {
        byte[] pdf = reporteService.puestosPorMesPdf(anio, mes);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"reporte-puestos-" + anio + "-" + mes + ".pdf\"");
        response.setContentLength(pdf.length);
        response.getOutputStream().write(pdf);
        response.getOutputStream().flush();
    }
}