package progra4.prestamos.controller;

import progra4.prestamos.model.Puesto;
import progra4.prestamos.service.CaracteristicaService;
import progra4.prestamos.service.OferenteService;
import progra4.prestamos.service.PuestoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/empresa")
@RequiredArgsConstructor
public class EmpresaController {

    private final PuestoService puestoService;
    private final CaracteristicaService caracteristicaService;
    private final OferenteService oferenteService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "empresa/dashboard";
    }

    @GetMapping("/puestos")
    public String misPuestos(Model model) {
        model.addAttribute("puestos", puestoService.puestosDeEmpresaActual());
        return "empresa/mis-puestos";
    }

    @GetMapping("/puestos/nuevo")
    public String nuevoPuestoForm(Model model) {
        model.addAttribute("puesto", new Puesto());
        model.addAttribute("caracteristicas", caracteristicaService.todasJerarquicas());
        return "empresa/nuevo-puesto";
    }

    @PostMapping("/puestos/nuevo")
    public String guardarPuesto(@ModelAttribute Puesto puesto,
                                @RequestParam List<Integer> caracIds,
                                @RequestParam List<Integer> niveles) {
        puestoService.publicar(puesto, caracIds, niveles);
        return "redirect:/empresa/puestos";
    }

    @PostMapping("/puestos/{id}/desactivar")
    public String desactivar(@PathVariable Integer id) {
        puestoService.desactivar(id);
        return "redirect:/empresa/puestos";
    }

    @GetMapping("/candidatos/buscar")
    public String buscarCandidatos(@RequestParam Integer puestoId, Model model) {
        model.addAttribute("puesto", puestoService.obtener(puestoId));
        model.addAttribute("candidatos", oferenteService.buscarCandidatos(puestoId));
        return "empresa/candidatos";
    }

    @GetMapping("/candidatos/{id}")
    public String verCandidato(@PathVariable Integer id, Model model) {
        model.addAttribute("oferente", oferenteService.obtener(id));
        return "empresa/detalle-candidato";
    }
}