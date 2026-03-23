// ============================================================
// PublicoController.java
// ============================================================
package progra4.prestamos.controller;

import progra4.prestamos.service.CaracteristicaService;
import progra4.prestamos.service.PuestoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PublicoController {

    private final PuestoService puestoService;
    private final CaracteristicaService caracteristicaService;

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("puestos", puestoService.ultimos5Publicos());
        return "public/inicio";
    }

    @GetMapping("/puestos/buscar")
    public String buscarForm(Model model) {
        model.addAttribute("caracteristicas", caracteristicaService.todasJerarquicas());
        return "public/buscar-puestos";
    }

    @PostMapping("/puestos/buscar")
    public String buscarResultados(@RequestParam(required = false) List<Integer> caracteristicaIds,
                                   Model model) {
        model.addAttribute("caracteristicas", caracteristicaService.todasJerarquicas());
        model.addAttribute("resultados", puestoService.buscarPorCaracteristicas(caracteristicaIds));
        model.addAttribute("selectedIds", caracteristicaIds);
        return "public/buscar-puestos";
    }

    @GetMapping("/login")
    public String login() {
        return "public/login";
    }

    // Redirigir /dashboard según el rol
    @GetMapping("/dashboard")
    public String dashboard() {
        var auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() ||
                auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
            return "redirect:/";
        }

        boolean isAdmin   = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isEmpresa = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPRESA"));
        boolean isOferente = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_OFERENTE"));

        if (isAdmin)    return "redirect:/admin/dashboard";
        if (isEmpresa)  return "redirect:/empresa/dashboard";
        if (isOferente) return "redirect:/oferente/dashboard";

        return "redirect:/";
    }
}