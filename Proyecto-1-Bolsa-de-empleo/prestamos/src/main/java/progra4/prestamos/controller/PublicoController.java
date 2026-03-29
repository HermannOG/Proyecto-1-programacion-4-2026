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
import progra4.prestamos.service.AplicacionService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PublicoController {

    private final PuestoService puestoService;
    private final CaracteristicaService caracteristicaService;
    private final AplicacionService    aplicacionService;

    @GetMapping("/")
    public String inicio(Model model) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        boolean autenticado = auth != null && auth.isAuthenticated() &&
                !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken);

        boolean esOferenteAprobado = autenticado && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_OFERENTE"));

        if (esOferenteAprobado) {
            model.addAttribute("puestos", puestoService.todosActivos());
        } else if (autenticado) {
            model.addAttribute("puestos", puestoService.soloPublicosActivos());
        } else {
            model.addAttribute("puestos", puestoService.ultimos5Publicos());
        }
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

    @GetMapping("/dashboard")
    public String dashboard(jakarta.servlet.http.HttpSession session) {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            return "redirect:/";
        }

        // Redirigir al destino guardado si existe
        String destino = (String) session.getAttribute("destino_post_login");
        if (destino != null) {
            session.removeAttribute("destino_post_login");
            return "redirect:" + destino;
        }

        boolean isAdmin    = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isEmpresa  = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPRESA"));
        boolean isOferente = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_OFERENTE"));

        if (isAdmin)    return "redirect:/admin/dashboard";
        if (isEmpresa)  return "redirect:/empresa/dashboard";
        if (isOferente) return "redirect:/oferente/dashboard";

        return "redirect:/";
    }

    @GetMapping("/puestos/{id}")
    public String detallePuesto(@PathVariable Integer id, Model model) {
        model.addAttribute("puesto", puestoService.obtener(id));
        model.addAttribute("yaAplico", aplicacionService.yaAplico(id));
        return "public/detalle-puesto";
    }

    @PostMapping("/puestos/{id}/aplicar")
    public String aplicar(@PathVariable Integer id,
                          jakarta.servlet.http.HttpSession session) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean autenticado = auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);
        boolean esOferente  = autenticado && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_OFERENTE"));

        if (!autenticado || !esOferente) {
            // Guardar destino para redirigir tras login
            session.setAttribute("destino_post_login", "/puestos/" + id);
            return "redirect:/login?aplicar=true";
        }

        try {
            aplicacionService.aplicar(id);
            return "redirect:/puestos/" + id + "?aplicacionOk=true";
        } catch (IllegalStateException e) {
            return "redirect:/puestos/" + id + "?yaAplico=true";
        }
    }
}