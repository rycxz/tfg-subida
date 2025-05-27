/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package kiricasa.programa.controller;

import java.util.List;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import kiricasa.programa.models.FavoritosModel;
import kiricasa.programa.models.PublicacionModel;
import kiricasa.programa.models.UsuarioModel;
import kiricasa.programa.repository.BarriosRepository;
import kiricasa.programa.repository.FavoritosRepository;
import kiricasa.programa.repository.PublicacionRepository;
import kiricasa.programa.repository.UsuarioRepository;
import kiricasa.programa.service.CorreoService;
import lombok.AllArgsConstructor;

/**
 *
 * @author 6003194
 */
@Controller
@RequestMapping("/perfil")
@AllArgsConstructor
public class PerfilController {
    private final  PublicacionRepository publicacionRepository;
    private final FavoritosRepository favoritosRepository;
    private final  UsuarioRepository usuarioRepository;
    private final CorreoService emailService;
    private final  PasswordEncoder passwordEncoder;
    private final  BarriosRepository barriosRepository;

         /**
          * Método para mostrar el perfil del usuario.
          * @param model
          * @param session
          * @return
          */
       @GetMapping("/ver")
         public String verPerfil(Model model, HttpSession session) {
              String token = (String) session.getAttribute("jwt");
              //hay que obtener el usuario logueado,sus publicaciones y sus favoritos
              UsuarioModel usuarioLogueado = (UsuarioModel) session.getAttribute("usuario");
            List<PublicacionModel> publicaciones = publicacionRepository.findByUsuario(usuarioLogueado);
            List<FavoritosModel> favoritos = favoritosRepository.findByUsuario(usuarioLogueado);
            if (usuarioLogueado == null || token == null) {
                return "redirect:/nl/home";
              }
                model.addAttribute("barrios", barriosRepository.findAll());
                model.addAttribute("usuario", usuarioLogueado);
                model.addAttribute("publicaciones", publicaciones);
                System.out.println("Publicaciones del usuario: " + publicaciones.get(0).getImagePrincipal() + "-------------------------------------------------------------------------------------------------------------------------");
                model.addAttribute("favoritos", favoritos);
                return "perfil";
         }
        @GetMapping("/editar")
        /**
         * * Muestra el formulario de edición del perfil
         * @param session
         * @param model
         * @return
         */
        public String mostrarFormularioEdicion(HttpSession session, Model model) {
            UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
            String token = (String) session.getAttribute("jwt");

            if (usuario == null || token == null) {
                return "redirect:/nl/home";
            }
             model.addAttribute("barrios", barriosRepository.findAll());
            model.addAttribute("usuario", usuario);
            return "perfil_editar"; // Vista con el formulario
        }
        @PostMapping("/editar")
        /**
         *  * Procesa la edición del perfil
         * @param usuarioForm
         * @param session
         * @param redirectAttributes
         * @param model
         * @return
         */
        public String procesarEdicionPerfil(@ModelAttribute UsuarioModel usuarioForm,
                                            HttpSession session,
                                            RedirectAttributes redirectAttributes,Model model) {

            UsuarioModel usuarioSesion = (UsuarioModel) session.getAttribute("usuario");

            if (usuarioSesion == null) {
                return "redirect:/nl/home";
            }

            // Copiar solo los campos editables que quieras permitir
            usuarioSesion.setNombre(usuarioForm.getNombre());
            usuarioSesion.setEmail(usuarioForm.getEmail());
            // Añade aquí cualquier otro campo editable

            usuarioRepository.save(usuarioSesion);
            session.setAttribute("usuario", usuarioSesion); // Actualizar en sesión
            model.addAttribute("barrios", barriosRepository.findAll());
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente.");
            return "redirect:/perfil/ver";
        }
        @GetMapping("/2fa")
        /**
         * * Muestra el formulario de verificación de 2FA
         * @param session
         * @param model
         * @return
         */
        public String mostrarFormularioVerificacion(HttpSession session, Model model) {
            UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
            if (usuario == null) return "redirect:/auth/login";

            // Generar código y simular envío
            String codigo = String.format("%06d", new Random().nextInt(999999));
            session.setAttribute("codigoVerificacion", codigo);

           emailService.enviarCodigo(usuario.getEmail(), codigo);
             model.addAttribute("barrios", barriosRepository.findAll());
            model.addAttribute("usuario", usuario);
            return "perfil_verificar";
        }
        @PostMapping("/2fa")
        /**
         * * Procesa el código de verificación
         * @param codigo
         * @param session
         * @param redirectAttributes
         * @param model
         * @return
         */
        public String procesarCodigoVerificacion(@RequestParam("codigo") String codigo,
                                                HttpSession session,
                                                RedirectAttributes redirectAttributes ,Model model) {
            UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
            if (usuario == null) return "redirect:/auth/login";

            String codigoGuardado = (String) session.getAttribute("codigoVerificacion");
             model.addAttribute("barrios", barriosRepository.findAll());
            if (codigo.equals(codigoGuardado)) {
                usuario.setVerificado(true);
                usuarioRepository.save(usuario);
                session.setAttribute("usuario", usuario);
                redirectAttributes.addFlashAttribute("success", "Cuenta verificada correctamente.");
                return "redirect:/perfil/ver";
            } else {
                redirectAttributes.addFlashAttribute("error", "Código incorrecto. Intenta de nuevo.");
                return "redirect:/perfil/ver";
            }
        }
        @GetMapping("/cambiarPW")
        /**
         * * Muestra el formulario para cambiar la contraseña
         * @param model
         * @param session
         * @return
         */
        public String mostrarFormularioCambioPassword(Model model, HttpSession session) {
              UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
              if (usuario == null) return "redirect:/auth/login";
               model.addAttribute("barrios", barriosRepository.findAll());
              model.addAttribute("usuario", usuario);
            return "perfil_cambiarPW";
        }
        @PostMapping("/cambiarPW")
        /**
         * * Procesa el cambio de contraseña
         */
            public String procesarCambioPassword(@RequestParam String actualPassword,
                                                @RequestParam String nuevaPassword,
                                                @RequestParam String confirmarPassword,
                                                HttpSession session,
                                                RedirectAttributes redirectAttributes,Model model) {
                UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");

                if (usuario == null) return "redirect:/auth/login";
                    model.addAttribute("barrios", barriosRepository.findAll());
                // Validar contraseña actual
                if (!passwordEncoder.matches(actualPassword, usuario.getPassword())) {
                    redirectAttributes.addFlashAttribute("error", "La contraseña actual no es correcta.");
                    return "redirect:/perfil/cambiarPW";
                }

                // Validar longitud mínima
                if (nuevaPassword.length() < 6) {
                    redirectAttributes.addFlashAttribute("error", "La nueva contraseña debe tener al menos 6 caracteres.");
                    return "redirect:/perfil/cambiarPW";
                }

                // Validar coincidencia
                if (!nuevaPassword.equals(confirmarPassword)) {
                    redirectAttributes.addFlashAttribute("error", "Las nuevas contraseñas no coinciden.");
                    return "redirect:/perfil/cambiarPW";
                }

                // Actualizar contraseña
                usuario.setPassword(passwordEncoder.encode(nuevaPassword));
                usuarioRepository.save(usuario);
                redirectAttributes.addFlashAttribute("success", "Contraseña actualizada correctamente.");
                return "redirect:/perfil/ver";
            }
        @GetMapping("/logout")
        /**
         * * Cierra la sesión del usuario
         * @param session
         * @param model
         * @return
         */
            public String cerrarSesion(HttpSession session,Model model) {
                session.invalidate();
                  model.addAttribute("logout", "Sesión cerrada correctamente.");
                return "redirect:/nl/home";
            }




}
