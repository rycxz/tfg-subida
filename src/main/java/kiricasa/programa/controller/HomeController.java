package kiricasa.programa.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import kiricasa.programa.enums.TipoPiso;
import kiricasa.programa.models.PublicacionModel;
import kiricasa.programa.repository.BarriosRepository;
import kiricasa.programa.repository.PublicacionRepository;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping
@AllArgsConstructor
public class HomeController {

    private final PublicacionRepository publicacionRepository;
    private final BarriosRepository barriosRepository;

    @GetMapping("/home")
    /**
     * * Muestra la vista de inicio
     *
     * @param barrio
     * @param tipo
     * @param model
     * @param session
     * @return
     */
    public String mostrarHome(
            @RequestParam(required = false) String barrio,
            @RequestParam(required = false) String tipo,
            Model model,
            HttpSession session
    ) {
        // Sesión / token
        String token   = (String) session.getAttribute("jwt");
        Object usuario = session.getAttribute("usuario");
        if (usuario == null || token == null) {
            return "redirect:/nl/home";
        }

        // Datos comunes
        model.addAttribute("barrios", barriosRepository.findAll());
        model.addAttribute("usuario", usuario);
        model.addAttribute("tipos", List.of("Hombres","Mujeres","Mixto","Solo"));

        List<PublicacionModel> publicaciones;

        if (tipo != null && !tipo.isBlank()) {
            try {
                // Convertir de String a enum
                TipoPiso tipoEnum = TipoPiso.valueOf(tipo.trim().toUpperCase());
                publicaciones = publicacionRepository.findByTipo(tipoEnum);
            } catch (IllegalArgumentException ex) {
                // Valor de tipo no válido -> ningún resultado
                publicaciones = List.of();
            }
        }
        else if (barrio != null && !barrio.isBlank()) {
            publicaciones = publicacionRepository.buscarConFiltros(
                null, null, null, null, null, null, null, null, barrio
            );
        }
        else {
            publicaciones = publicacionRepository.findAll();
        }

        // Primera foto como principal
        for (PublicacionModel pub : publicaciones) {
            if (!pub.getFotos().isEmpty()) {
                pub.setImagen(pub.getFotos().get(0));
            }
        }

        model.addAttribute("publicaciones", publicaciones);
        return "home";
    }
}
