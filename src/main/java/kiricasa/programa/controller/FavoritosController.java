/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package kiricasa.programa.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import kiricasa.programa.models.FavoritosModel;
import kiricasa.programa.models.PublicacionModel;
import kiricasa.programa.models.UsuarioModel;
import kiricasa.programa.repository.BarriosRepository;
import kiricasa.programa.repository.FavoritosRepository;
import kiricasa.programa.repository.PublicacionRepository;
import lombok.AllArgsConstructor;

/**
 *
 * @author 6003194
 */

 @Controller
@RequestMapping("/favoritos")
@AllArgsConstructor
public class FavoritosController {
        private final FavoritosRepository favoritosRepository;
    private final PublicacionRepository publicacionRepository;
    private final BarriosRepository barriosRepository;

    @PostMapping("/añadir")
    /**
     * * Añade una publicación a favoritos
     * @param id
     * @param redirectAttributes
     * @param session
     * @return
     */
    @Transactional
    public String añadirFavorito(@RequestParam("publicacionId") Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login"; // o donde prefieras redirigir si no está logueado
        }
            PublicacionModel publicacion = publicacionRepository.findById(id).orElse(null);
        if (publicacion == null) {
            redirectAttributes.addFlashAttribute("error", "Publicación no encontrada.");
            return "redirect:/home";
        }
         boolean yaExiste = favoritosRepository.findByUsuarioAndPublicacion(usuario, publicacion).isPresent();
        if (!yaExiste) {
            // Guardar favorito
            FavoritosModel favorito = new FavoritosModel();
            favorito.setUsuario(usuario);
            favorito.setPublicacion(publicacion);
            favoritosRepository.save(favorito);
            redirectAttributes.addFlashAttribute("success", "Añadido a favoritos.");
        } else {
            redirectAttributes.addFlashAttribute("info", "Este anuncio ya está en tus favoritos.");
        }
         return "redirect:/detalle?id=" + id;
    }

@PostMapping("/eliminar")
/**
 * * Elimina una publicación de favoritos
 * @param id
 * @param redirectAttributes
 * @param session
 * @return
 */
@Transactional
public String eliminarFavorito(@RequestParam("publicacionId") Long id, RedirectAttributes redirectAttributes, HttpSession session) {
    UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
    if (usuario == null) {
        return "redirect:/auth/login";
    }

    PublicacionModel publicacion = publicacionRepository.findById(id).orElse(null);
    if (publicacion == null) {
        redirectAttributes.addFlashAttribute("error", "Publicación no encontrada.");
        return "redirect:/home";
    }

    boolean yaExiste = favoritosRepository.findByUsuarioAndPublicacion(usuario, publicacion).isPresent();
    if (yaExiste) {
        favoritosRepository.deleteByUsuarioAndPublicacion(usuario, publicacion);
        redirectAttributes.addFlashAttribute("success", "Eliminado de favoritos.");
    } else {
        redirectAttributes.addFlashAttribute("info", "Este anuncio no estaba en tus favoritos.");
    }

    return "redirect:/detalle?id=" + id;
}
    @GetMapping("/ver")
    /**
     * * * Muestra la vista de favoritos
     * @param model
     * @param session
     * @return
     */
    public String verFavoritos(Model model, HttpSession session) {
        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        List<FavoritosModel> favoritos = favoritosRepository.findByUsuario(usuario);
         model.addAttribute("barrios", barriosRepository.findAll());
        model.addAttribute("usuario", usuario);
        model.addAttribute("favoritos", favoritos);
        return "favoritos";
    }
    @PostMapping("/eliminar1")
    /**
     * * Elimina una publicación de favoritos
     * @param id
     * @param session
     * @param redirectAttributes
     * @return
     */
    @Transactional
    public String eliminarDesdeFavoritos(@RequestParam("publicacionId") Long id,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {

        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        PublicacionModel publicacion = publicacionRepository.findById(id).orElse(null);
        if (publicacion == null) {
            redirectAttributes.addFlashAttribute("error", "Publicación no encontrada.");
            return "redirect:/favoritos/ver";
        }

        boolean yaExiste = favoritosRepository.findByUsuarioAndPublicacion(usuario, publicacion).isPresent();
        if (yaExiste) {
            favoritosRepository.deleteByUsuarioAndPublicacion(usuario, publicacion);
            redirectAttributes.addFlashAttribute("success", "Anuncio eliminado de favoritos.");
        } else {
            redirectAttributes.addFlashAttribute("info", "Este anuncio ya no estaba en tus favoritos.");
        }

        return "redirect:/favoritos/ver";
    }




}
