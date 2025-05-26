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
    @GetMapping("/editar/{id}")
    /**
     * Método para mostrar el formulario de edición de una publicación.
     * @param id
     * @param model
     * @param session
     * @return
     */
        public String mostrarFormularioEdicion(@PathVariable("id") Long id, Model model, HttpSession session) {
        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/auth/login";

        PublicacionModel publicacion = publicacionRepository.findById(id).orElse(null);
        if (publicacion == null) return "redirect:/home";

        // Solo dueño o admin
        if (!usuario.getRol().equals(UsuarioRol.ADMIN) && !publicacion.getUsuario().getId().equals(usuario.getId())) {
            return "redirect:/home";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("publicacion", publicacion);
        model.addAttribute("barrios", barriosRepository.findAll());

        return "publicacion_editar"; // vista
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
        /**
         * Método para subir una imagen a una publicación.
         * @param id
         * @param archivo
         * @param redirectAttributes
         * @param model
         * @return
         */
    public String subirImagen(@PathVariable Long id,
                            @RequestParam("imagen") MultipartFile archivo,
                            RedirectAttributes redirectAttributes,Model model) {



        PublicacionModel publicacion = publicacionRepository.findById(id).orElse(null);
        if (publicacion == null) {
            redirectAttributes.addFlashAttribute("error", "La publicación no existe.");
            return "redirect:/home";
        }

        if (archivo == null || archivo.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No se seleccionó ninguna imagen.");
            return "redirect:/detalle?id=" + id;
        }

        // Reasignar carpeta si es la predeterminada
        String carpetaActual = publicacion.getCarpetaImagen();
        if (carpetaActual.equals("pisos_auto")) {
            carpetaActual = "publicacion_" + id;
            publicacion.setCarpetaImagen(carpetaActual);

        }


 String basePath = "C:/Users/recur/Desktop/TFG/Codigo/programa/uploads/publicaciones/";



        String carpetaFinal = basePath + carpetaActual;
        File directorio = new File(carpetaFinal);
        if (!directorio.exists()) {
            @SuppressWarnings("unused")
            boolean creado = directorio.mkdirs();

        }

        try {
            String nombreArchivo = archivo.getOriginalFilename();

            if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {

                redirectAttributes.addFlashAttribute("error", "Nombre de archivo inválido.");
                return "redirect:/detalle?id=" + id;
            }

            Path ruta = Paths.get(carpetaFinal, nombreArchivo);


            archivo.transferTo(ruta.toFile());

            // Actualizar primer hueco vacío
                for (int i = 0; i < 9; i++) {
            String imgActual = publicacion.getImagenPorIndice(i);
            if (imgActual == null || imgActual.isEmpty() || imgActual.equals("predeterminada.png")) {
                publicacion.setImagenPorIndice(i, nombreArchivo);
                break;
            }
        }
               publicacionRepository.save(publicacion);
            redirectAttributes.addFlashAttribute("success", "Imagen subida correctamente.");

        } catch (IOException e) {
            System.out.println("❌ Error al guardar imagen en disco");
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al guardar la imagen.");
        }
         model.addAttribute("barrios", barriosRepository.findAll());
        return "redirect:/detalle?id=" + id;
    }

    @GetMapping("/editar/{id}/eliminar-imagen")
    /**
     * Método para eliminar una imagen de una publicación.
     * @param id
     * @param nombre
     * @param redirectAttributes
     * @param model
     * @return
     */
    public String eliminarImagen(@PathVariable Long id,
                                @RequestParam("imagen") String nombre,
                                RedirectAttributes redirectAttributes,Model model) {

        PublicacionModel publicacion = publicacionRepository.findById(id).orElse(null);
        if (publicacion == null) {
            redirectAttributes.addFlashAttribute("error", "Publicación no encontrada.");
            return "redirect:/home";
        }

        // Detectar la posición de la imagen en la lista original
        List<String> imagenes = publicacion.getFotosOriginales();
        int posicion = imagenes.indexOf(nombre);

        if (posicion != -1) {

            publicacion.setImagenPorIndice(posicion,"" );
            publicacionRepository.save(publicacion);

            // Eliminar físicamente el archivo
           String ruta = "C:/Users/recur/Desktop/TFG/Codigo/programa/uploads/publicaciones/"



                        + publicacion.getCarpetaImagen() + "/" + nombre;

            File archivo = new File(ruta);
            if (archivo.exists()) archivo.delete();

            redirectAttributes.addFlashAttribute("success", "Imagen eliminada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Imagen no encontrada.");
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

    // Verificar permisos
    if (!usuario.getId().equals(publicacion.getUsuario().getId()) && usuario.getRol() != UsuarioRol.ADMIN) {
        redirectAttributes.addFlashAttribute("error", "No tienes permisos para editar esta publicación.");
        return "redirect:/home";
    }

    // Actualizar los datos
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
    /**
     * Método para publicar una nueva publicación.
     * @param titulo
     * @param descripcion
     * @param precio
     * @param estado
     * @param tipo
     * @param ubicacion
     * @param metrosCuadrados
     * @param habitaciones
     * @param permiteMascotas
     * @param numeroCompañeros
     * @param barrioId
     * @param imagenes
     * @param redirectAttributes
     * @param session
     * @param model
     * @return
     */
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
                                        HttpSession session,Model model) {

        UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        String token = (String) session.getAttribute("jwt");
        if (usuario == null || token == null) {
            redirectAttributes.addFlashAttribute("error", "Sesión expirada.");
            return "redirect:/auth/login";
        }
        // Crear publicación con imágenes predeterminadas
        PublicacionModel publicacion = new PublicacionModel();
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
        publicacion.setUsuario(usuario);
        barriosRepository.findById(barrioId).ifPresent(publicacion::setBarrio);
        // Inicializar imágenes con predeterminada
        for (int i = 0; i < 9; i++) {
            publicacion.setImagenPorIndice(i, "predeterminada.png");
        }

        // Guardar para generar el ID (carpeta)
        publicacionRepository.save(publicacion);

        // Crear carpeta en disco
        String carpeta = "publicacion_" + publicacion.getId();
        publicacion.setCarpetaImagen(carpeta);
    String basePath = "C:/Users/recur/Desktop/TFG/Codigo/programa/uploads/publicaciones/";



        String rutaFinal = basePath + carpeta;
        File carpetaDir = new File(rutaFinal);
        if (!carpetaDir.exists()) carpetaDir.mkdirs();

        // Subir imágenes
        List<String> nombresImagenes = new ArrayList<>();
        int contador = 0;

        for (MultipartFile imagen : imagenes) {
            if (imagen != null && !imagen.isEmpty() && contador < 9) {
                try {
                    String nombreOriginal = imagen.getOriginalFilename();
                    Path destino = Paths.get(rutaFinal, nombreOriginal);
                    imagen.transferTo(destino.toFile());
                    nombresImagenes.add(nombreOriginal);
                    contador++;
                } catch (IOException e) {
                    e.printStackTrace();
                    redirectAttributes.addFlashAttribute("error", "Error al guardar imagen: " + imagen.getOriginalFilename());
                    System.out.println("❌ Error al guardar imagen en disco");
                    return "redirect:/publicacion/nueva";
                }
            }
            return "redirect:/home";
        }
    model.addAttribute("barrios", barriosRepository.findAll());
        // Reasignar imágenes subidas (si hay)
        for (int i = 0; i < nombresImagenes.size(); i++) {
            publicacion.setImagenPorIndice(i, nombresImagenes.get(i));
        }

        // Guardar cambios definitivos
        publicacionRepository.save(publicacion);

        redirectAttributes.addFlashAttribute("success", "Anuncio publicado correctamente.");
        return "redirect:/perfil/ver";
    }
    @GetMapping("/eliminar/{id}")
  /**
   * Método para eliminar una publicación.
   * @param id                 ID de la publicación a eliminar.
   */
    public String eliminarPublicacion(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            HttpSession session
    ) {
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

        // *** 1) eliminar hijos en anuncios_vistos ***
        anuncioVistoRepository.deleteByPublicacionId(pub.getId());

        // *** 2) borrar BD y carpeta igual que antes ***
        publicacionRepository.delete(pub);
        // … código para eliminar carpeta de imágenes …

        redirectAttributes.addFlashAttribute("success", "Publicación eliminada correctamente.");
        return "redirect:/home";
    }


}
