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
@RequestMapping("/nl")
@AllArgsConstructor
public class NoLoggedController {

    private final PublicacionRepository publicacionRepository;
    private final BarriosRepository barriosRepository;

    /**
     * Muestra el home de usuario no logueado, opcionalmente filtrado por barrio o tipo.
     */
    @GetMapping("/home")
    public String mostrarHome(
            @RequestParam(required = false) String barrio,
            @RequestParam(required = false) String tipo,
            Model model,
            HttpSession session
    ) {
        // --- Filtrado de publicaciones ---
        List<PublicacionModel> publicaciones;
        if (tipo != null && !tipo.isBlank()) {
            try {
                // Convertimos la cadena al enum
                TipoPiso tipoEnum = TipoPiso.valueOf(tipo.trim().toUpperCase());
                publicaciones = publicacionRepository.findByTipo(tipoEnum);
            } catch (IllegalArgumentException ex) {
                // Si el valor no es válido, devolvemos lista vacía
                publicaciones = List.of();
            }
        }
        else if (barrio != null && !barrio.isBlank()) {
            publicaciones = publicacionRepository.buscarConFiltros(
                null, null, null, null, null,
                null, null, null, barrio
            );
        }
        else {
            publicaciones = publicacionRepository.findAll();
        }

        // --- Seleccionar la primera foto como imagen principal ---
        for (PublicacionModel pub : publicaciones) {
            if (!pub.getFotos().isEmpty()) {
                pub.setImagen(pub.getFotos().get(0));
            }
        }

        // --- Añadir al modelo ---
        model.addAttribute("publicaciones", publicaciones);
        model.addAttribute("barrios", barriosRepository.findAll());
        model.addAttribute("tipos", List.of("Hombres", "Mujeres", "Mixto", "Solo"));
        // Para no logueado, mantenemos usuario a null en la vista
        model.addAttribute("usuario", null);

        return "home";
    }

    /**
     * Vista de detalle para no logueados.
     */
    @GetMapping("/detalle")

    public String verDetalle(
            @RequestParam("id") Long id,
            Model model
    ) {
        PublicacionModel publicacion = publicacionRepository.findById(id).orElse(null);
        if (publicacion == null) {
            // redirige al home no logueado si no existe
            return "redirect:/nl/home";
        }

        // Cargamos barrios y tipos (para mantener el menú)
        model.addAttribute("barrios", barriosRepository.findAll());
        model.addAttribute("tipos", List.of("Hombres", "Mujeres", "Mixto", "Solo"));

        // Fotos de la publicación
        List<String> fotos = publicacion.getFotos();
        model.addAttribute("fotos", fotos);
        model.addAttribute("publicacion", publicacion);

        // Usuario no logueado => no puede gestionar ni tener favoritos
        model.addAttribute("usuario", null);
        model.addAttribute("puedeGestionar", false);

        return "publicacion";
    }
}
