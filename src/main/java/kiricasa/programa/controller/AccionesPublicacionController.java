/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package kiricasa.programa.controller;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import kiricasa.programa.enums.TipoPiso;
import kiricasa.programa.enums.UsuarioRol;
import kiricasa.programa.models.BarriosModel;
import kiricasa.programa.models.PublicacionModel;
import kiricasa.programa.models.UsuarioModel;
import kiricasa.programa.repository.AnunciosVistosRepository;
import kiricasa.programa.repository.BarriosRepository;
import kiricasa.programa.repository.PublicacionRepository;
import lombok.AllArgsConstructor;

/**
 *
 * @author 6003194
 */
/**
 * controlador para gestionar las acciones relacionadas con las publicaciones.
 * permite editar, eliminar, crear y gestionar imagenes de publicaciones.
 *
 *
 * Requiere que el usuario esté autenticado y tenga permisos adecuados (dueño o admin).

 * @author recur
 */
@Controller
@RequestMapping("/publicacion")
@AllArgsConstructor
public class AccionesPublicacionController {
    private final PublicacionRepository publicacionRepository;
    private final BarriosRepository barriosRepository;
    private final AnunciosVistosRepository anuncioVistoRepository;


    /**
     * Método para mostrar el formulario de edición de una publicación.
     * @param id
     * @param model
     * @param session
     * @return
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable("id") Long id, Model model, HttpSession session) {
        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/auth/login";

        PublicacionModel publicacion = publicacionRepository.findById(id).orElse(null);
        if (publicacion == null) return "redirect:/home";

        if (!usuario.getRol().equals(UsuarioRol.ADMIN) && !publicacion.getUsuario().getId().equals(usuario.getId())) {
            return "redirect:/home";
        }
        List<String> fotos = new ArrayList<>(publicacion.getFotos());
        while (fotos.size() < 9) {
            fotos.add("predeterminada.png");
        }
model.addAttribute("fotosCompletas", fotos);

        model.addAttribute("usuario", usuario);
        model.addAttribute("publicacion", publicacion);
        model.addAttribute("barrios", barriosRepository.findAll());

        return "publicacion_editar";
    }

    /**
     * Método para subir una imagen a una publicación.
     *
     * @param id                 ID de la publicación.
     * @param archivo            Archivo de imagen a subir.
     * @param redirectAttributes Atributos para redirección.
     * @return Redirección a la página de edición de la publicación.
     *
     * @param id
     * @param archivo
     * @param redirectAttributes
     * @return
     */
@PostMapping("/editar/{id}/subir-imagen")
@SuppressWarnings("CallToPrintStackTrace")
public String subirImagen(@PathVariable Long id,
                          @RequestParam("imagen") MultipartFile archivo,
                          RedirectAttributes redirectAttributes, Model model) {
    PublicacionModel publicacion = publicacionRepository.findById(id).orElse(null);
    if (publicacion == null) {
        redirectAttributes.addFlashAttribute("error", "La publicación no existe.");
        return "redirect:/home";
    }

    if (archivo == null || archivo.isEmpty()) {
        redirectAttributes.addFlashAttribute("error", "No se seleccionó ninguna imagen.");
        return "redirect:/detalle?id=" + id;
    }

    try {

        String carpeta = publicacion.getCarpetaImagen();
         if (carpeta == null || carpeta.isEmpty()) {
            carpeta = "publicacion_" + publicacion.getId();
            publicacion.setCarpetaImagen(carpeta);
        }

        String rutaBase = new File("src/main/resources/static/images/uploads/publicaciones/").getAbsolutePath();
        File carpetaDir = new File(rutaBase, carpeta);
        if (!carpetaDir.exists()) carpetaDir.mkdirs();


        String nombreOriginal = archivo.getOriginalFilename();
        Path destino = Paths.get(carpetaDir.getAbsolutePath(), nombreOriginal);
        archivo.transferTo(destino.toFile());


    String rutaRelativa = nombreOriginal;


        for (int i = 0; i < 9; i++) {
            String imgActual = publicacion.getImagenPorIndice(i);
            if (imgActual == null || imgActual.isEmpty() || imgActual.equals("predeterminada.png")) {
                publicacion.setImagenPorIndice(i, rutaRelativa);
                break;
            }
        }

        publicacionRepository.save(publicacion);
        redirectAttributes.addFlashAttribute("success", "Imagen subida correctamente.");
    } catch (IOException e) {
        e.printStackTrace();
        redirectAttributes.addFlashAttribute("error", "Error al guardar la imagen en disco.");
    }

    model.addAttribute("barrios", barriosRepository.findAll());
    return "redirect:/detalle?id=" + id;
}
    /**
     * Método para eliminar una imagen de una publicación.
     * @param id
     * @param nombre
     * @param redirectAttributes
     * @param model
     * @return
     */
@GetMapping("/editar/{id}/eliminar-imagen")
public String eliminarImagen(@PathVariable Long id,
                             @RequestParam("imagen") String nombre,
                             RedirectAttributes redirectAttributes, Model model) {
    PublicacionModel publicacion = publicacionRepository.findById(id).orElse(null);
    if (publicacion == null) {
        redirectAttributes.addFlashAttribute("error", "Publicación no encontrada.");
        return "redirect:/home";
    }

    List<String> imagenes = publicacion.getFotosOriginales();
    int posicion = imagenes.indexOf(nombre);

    if (posicion != -1) {

        String rutaBase = new File("src/main/resources/static/images/uploads/publicaciones/").getAbsolutePath();
            String carpeta = publicacion.getCarpetaImagen();
            File archivo = new File(rutaBase + "/" + carpeta, nombre);
        if (archivo.exists()) {
            if (archivo.delete()) {
                publicacion.setImagenPorIndice(posicion, "");
                publicacionRepository.save(publicacion);
                redirectAttributes.addFlashAttribute("success", "Imagen eliminada correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo eliminar la imagen del disco.");
            }
        } else {

            publicacion.setImagenPorIndice(posicion, "predeterminada.png");
            publicacionRepository.save(publicacion);
            redirectAttributes.addFlashAttribute("success", "Imagen eliminada de la publicación (no estaba en disco).");
        }
    } else {
        redirectAttributes.addFlashAttribute("error", "Imagen no encontrada en la publicación.");
    }

    model.addAttribute("barrios", barriosRepository.findAll());
    return "redirect:/detalle?id=" + id;
}
    /**
     * Método para mostrar el formulario de edición de una publicación.
     *
     * @param id                 ID de la publicación.
     * @param model              Modelo para la vista.
     * @param session            Sesión HTTP.
     * @param redirectAttributes Atributos para redirección.
     * @return Vista del formulario de edición.
     *
     */
@PostMapping("/editar/{id}/editarinfo")
    public String editarInfoPublicacion(@PathVariable Long id,
                                    @RequestParam String titulo,
                                    @RequestParam String descripcion,
                                    @RequestParam Integer precio,
                                    @RequestParam String estado,
                                    @RequestParam TipoPiso tipo,
                                    @RequestParam String ubicacion,
                                    @RequestParam int metrosCuadrados,
                                    @RequestParam String habitaciones,
                                    @RequestParam(required = false, defaultValue = "false") boolean permiteMascotas,
                                    @RequestParam int numeroCompañeros,
                                    @RequestParam Long barrioId,
                                    RedirectAttributes redirectAttributes,
                                    HttpSession session,Model model) {

    UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
    String token = (String) session.getAttribute("jwt");

    if (usuario == null || token == null) {
        redirectAttributes.addFlashAttribute("error", "Sesión expirada.");
        return "redirect:/nl/home";
    }
 model.addAttribute("barrios", barriosRepository.findAll());
    Optional<PublicacionModel> optionalPublicacion = publicacionRepository.findById(id);
    if (optionalPublicacion.isEmpty()) {
        redirectAttributes.addFlashAttribute("error", "La publicación no existe.");
        return "redirect:/home";
    }

    PublicacionModel publicacion = optionalPublicacion.get();


    if (!usuario.getId().equals(publicacion.getUsuario().getId()) && usuario.getRol() != UsuarioRol.ADMIN) {
        redirectAttributes.addFlashAttribute("error", "No tienes permisos para editar esta publicación.");
        return "redirect:/home";
    }


    publicacion.setTitulo(titulo);
    publicacion.setDescripcion(descripcion);
    publicacion.setPrecio(precio);
    publicacion.setEstado(estado);
    publicacion.setTipo(tipo);
    publicacion.setUbicacion(ubicacion);
    publicacion.setMetrosCuadrados(metrosCuadrados);
    publicacion.setHabitaciones(habitaciones);
    publicacion.setPermiteMascotas(permiteMascotas);
    publicacion.setNumeroCompañeros(numeroCompañeros);

    barriosRepository.findById(barrioId).ifPresent(publicacion::setBarrio);

    publicacionRepository.save(publicacion);

    redirectAttributes.addFlashAttribute("success", "Cambios guardados correctamente.");
    return "redirect:/detalle?id=" + id;

}
@GetMapping("/nueva")
public String mostrarFormularioPublicar(Model model, HttpSession session) {
            UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
            if (usuario == null) {
                return "redirect:/auth/login";
            }
            List<BarriosModel> barrios = barriosRepository.findAll();

            model.addAttribute("usuario", usuario);
            model.addAttribute("publicacion", new PublicacionModel());
            model.addAttribute("barrios", barrios);
            return "publicar";
        }
@PostMapping("/publicar")
@SuppressWarnings("CallToPrintStackTrace")
public String publicarNuevaPublicacion(@RequestParam String titulo,
                                       @RequestParam String descripcion,
                                       @RequestParam Integer precio,
                                       @RequestParam String estado,
                                       @RequestParam TipoPiso tipo,
                                       @RequestParam String ubicacion,
                                       @RequestParam int metrosCuadrados,
                                       @RequestParam String habitaciones,
                                       @RequestParam(required = false, defaultValue = "false") boolean permiteMascotas,
                                       @RequestParam int numeroCompañeros,
                                       @RequestParam Long barrioId,
                                       @RequestParam("imagenes") List<MultipartFile> imagenes,
                                       RedirectAttributes redirectAttributes,
                                       HttpSession session,
                                       Model model) {

    UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
    String token = (String) session.getAttribute("jwt");

    if (usuario == null || token == null) {
        redirectAttributes.addFlashAttribute("error", "Sesión expirada.");
        return "redirect:/auth/login";
    }

    // Crear nueva publicación
    PublicacionModel publicacion = new PublicacionModel();
    publicacion.setTitulo(titulo.trim());
    publicacion.setDescripcion(descripcion.trim());
    publicacion.setPrecio(precio);
    publicacion.setEstado(estado);
    publicacion.setTipo(tipo);
    publicacion.setUbicacion(ubicacion.trim());
    publicacion.setMetrosCuadrados(metrosCuadrados);
    publicacion.setHabitaciones(habitaciones.trim());
    publicacion.setPermiteMascotas(permiteMascotas);
    publicacion.setNumeroCompañeros(numeroCompañeros);
    publicacion.setUsuario(usuario);
    barriosRepository.findById(barrioId).ifPresent(publicacion::setBarrio);

    // Inicializar imágenes predeterminadas
    for (int i = 0; i < 9; i++) {
        publicacion.setImagenPorIndice(i, "predeterminada.png");
    }

    // Guardar inicialmente para obtener ID
    publicacionRepository.save(publicacion);

    // Crear carpeta en disco
    String carpeta = "publicacion_" + publicacion.getId();
    publicacion.setCarpetaImagen(carpeta);
    String rutaBase = new File("src/main/resources/static/images/uploads/publicaciones/").getAbsolutePath();
    String rutaFinal = rutaBase + "/" + carpeta;

    File carpetaDir = new File(rutaFinal);
    if (!carpetaDir.exists()) carpetaDir.mkdirs();

    // Guardar imágenes físicas
    List<String> nombresImagenes = new ArrayList<>();
    int contador = 0;

    for (MultipartFile imagen : imagenes) {
        if (imagen != null && !imagen.isEmpty() && contador < 9) {
            try {
                String nombreOriginal = imagen.getOriginalFilename();
                Path destino = Paths.get(rutaFinal, nombreOriginal);
                imagen.transferTo(destino.toFile());


                String rutaRelativa =   nombreOriginal;
                nombresImagenes.add(rutaRelativa);
                contador++;
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error", "Error al guardar imagen: " + imagen.getOriginalFilename());
                return "redirect:/publicacion/nueva";
            }
        }
    }


    for (int i = 0; i < nombresImagenes.size(); i++) {
        publicacion.setImagenPorIndice(i, nombresImagenes.get(i));
    }


    publicacionRepository.save(publicacion);

    redirectAttributes.addFlashAttribute("success", "Anuncio publicado correctamente.");
    return "redirect:/perfil/ver";
}
@GetMapping("/eliminar/{id}")
public String eliminarPublicacion(@PathVariable Long id,
                                  RedirectAttributes redirectAttributes,
                                  HttpSession session) {
    UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
    String token = (String) session.getAttribute("jwt");

    if (usuario == null || token == null) {
        redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para eliminar.");
        return "redirect:/nl/home";
    }

    var opt = publicacionRepository.findById(id);
    if (opt.isEmpty()) {
        redirectAttributes.addFlashAttribute("error", "La publicación no existe.");
        return "redirect:/home";
    }

    PublicacionModel pub = opt.get();
    boolean esAdmin = usuario.getRol() == UsuarioRol.ADMIN;
    boolean esDueno = pub.getUsuario().getId().equals(usuario.getId());
    if (!esAdmin && !esDueno) {
        redirectAttributes.addFlashAttribute("error", "No tienes permisos para eliminar esta publicación.");
        return "redirect:/home";
    }

    // 1. Eliminar hijos en anuncios_vistos
    anuncioVistoRepository.deleteByPublicacionId(pub.getId());

    // 2. Eliminar carpeta de imágenes si existe
    String carpeta = pub.getCarpetaImagen(); // debería ser tipo "publicacion_123"
    if (carpeta != null && !carpeta.isEmpty()) {
        File rutaBase = new File("src/main/resources/static/images/uploads/publicaciones");
        File carpetaPublicacion = new File(rutaBase, carpeta);
        if (carpetaPublicacion.exists() && carpetaPublicacion.isDirectory()) {
            // Eliminar todos los archivos dentro
            for (File archivo : carpetaPublicacion.listFiles()) {
                archivo.delete();
            }
            // Luego eliminar la carpeta
            carpetaPublicacion.delete();
        }
    }

    // 3. Eliminar de base de datos
    publicacionRepository.delete(pub);

    redirectAttributes.addFlashAttribute("success", "Publicación eliminada correctamente.");
    return "redirect:/home";
}


}
