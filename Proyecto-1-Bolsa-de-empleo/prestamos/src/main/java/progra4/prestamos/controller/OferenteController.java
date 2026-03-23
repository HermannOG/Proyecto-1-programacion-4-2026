package progra4.prestamos.controller;

import progra4.prestamos.service.CaracteristicaService;
import progra4.prestamos.service.OferenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/oferente")
@RequiredArgsConstructor
public class OferenteController {

    private final OferenteService oferenteService;
    private final CaracteristicaService caracteristicaService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "oferente/dashboard";
    }

    @GetMapping("/habilidades")
    public String habilidades(@RequestParam(required = false) Integer actualId, Model model) {
        model.addAttribute("habilidades", oferenteService.misHabilidades());
        model.addAttribute("caracteristicas", caracteristicaService.todasJerarquicas());
        if (actualId != null) {
            model.addAttribute("actual", caracteristicaService.obtener(actualId));
        }
        return "oferente/habilidades";
    }

    @PostMapping("/habilidades/agregar")
    public String agregarHabilidad(@RequestParam Integer caracId,
                                   @RequestParam Integer nivel) {
        oferenteService.agregarHabilidad(caracId, nivel);
        return "redirect:/oferente/habilidades";
    }

    @PostMapping("/habilidades/eliminar")
    public String eliminarHabilidad(@RequestParam Integer habilidadId) {
        oferenteService.eliminarHabilidad(habilidadId);
        return "redirect:/oferente/habilidades";
    }

    @GetMapping("/cv")
    public String miCV() {
        return "oferente/mi-cv";
    }

    @PostMapping("/cv")
    public String subirCV(@RequestParam MultipartFile archivo) {
        oferenteService.guardarCurriculum(archivo);
        return "redirect:/oferente/dashboard";
    }
}