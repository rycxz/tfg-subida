/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package kiricasa.programa.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import kiricasa.programa.models.PublicacionModel;
import kiricasa.programa.models.UsuarioModel;
import kiricasa.programa.repository.BarriosRepository;
import kiricasa.programa.repository.PublicacionRepository;
import lombok.AllArgsConstructor;

/**
 *
 * @author 6003194
 */
@Controller
@RequestMapping("/busqueda")
@AllArgsConstructor
public class BusquedaController {


       private final PublicacionRepository publicacionRepository;
         private final BarriosRepository barriosRepository;

        @GetMapping("/buscar")
        /**
         * * * Muestra la vista de búsqueda
         * @param query
         * @param model
         * @param session
         * @return
         */
         public String buscar(@RequestParam("query") String query, Model model,    HttpSession session) {
          UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("barrios", barriosRepository.findAll());

         model.addAttribute("usuario", usuario);

        List<PublicacionModel> resultados = publicacionRepository.findByTituloContainingIgnoreCaseOrUbicacionContainingIgnoreCase(query, query);
        model.addAttribute("resultados", resultados);
        model.addAttribute("query", query);
        return "resultados_busqueda";
    }

    @GetMapping("/filtros")
    /**
     * * Muestra la vista de búsqueda con filtros

     * @param titulo
     * @param ubicacion
     * @param estado
     * @param precioMax
     * @param metrosCuadradosMin
     * @param habitaciones
     * @param permiteMascotas
     * @param numeroCompañeros
     * @param barrio
     * @param model
     * @param session
     * @return
     */
    public String filtrarPublicaciones(
    @RequestParam(required = false) String titulo,
    @RequestParam(required = false) String ubicacion,
    @RequestParam(required = false) String estado,
    @RequestParam(required = false) String precioMax,
    @RequestParam(required = false) String metrosCuadradosMin,
    @RequestParam(required = false) String habitaciones,
    @RequestParam(required = false) Boolean permiteMascotas,
    @RequestParam(required = false) String numeroCompañeros,
    @RequestParam(required = false) String barrio,
    Model model, HttpSession session
) {
            // Conversiones seguras
    Integer precio = parseInteger(precioMax);
    Integer metros = parseInteger(metrosCuadradosMin);
    Integer companeros = parseInteger(numeroCompañeros);
     titulo     = (titulo == null || titulo.isBlank())      ? null : titulo.trim();
    ubicacion  = (ubicacion == null || ubicacion.isBlank()) ? null : ubicacion.trim();
    estado     = (estado == null || estado.isBlank())      ? null : estado.trim();
    habitaciones = (habitaciones == null || habitaciones.isBlank()) ? null : habitaciones.trim();
    barrio     = (barrio == null || barrio.isBlank())      ? null : barrio.trim();

        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
                if (usuario == null) {
                    return "redirect:/auth/login";
                }

           List<PublicacionModel> resultados = publicacionRepository.buscarConFiltros(
        titulo, ubicacion, estado, precio, metros,
        habitaciones, permiteMascotas, companeros, barrio
    );
 model.addAttribute("usuario", usuario);
            model.addAttribute("barrios", barriosRepository.findAll());
        model.addAttribute("resultados", resultados);
        return "resultados_busqueda";
    }

       @SuppressWarnings("UnnecessaryTemporaryOnConversionFromString")
       /**
        * Método auxiliar para convertir cadenas a enteros de forma segura
        * @param value
        * @return
        */
    private Integer parseInteger(String value) {
    try {
        return (value == null || value.isBlank()) ? null : Integer.parseInt(value.trim());
    } catch (NumberFormatException e) {
        return null;
    }
}
}

