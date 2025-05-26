/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package kiricasa.programa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import kiricasa.programa.models.UsuarioModel;
import kiricasa.programa.repository.BarriosRepository;
import lombok.AllArgsConstructor;

/**
 *
 * @author recur
 */
@Controller
@RequestMapping("/variado")
@AllArgsConstructor
public class VariadoController {


        private final  BarriosRepository barriosRepository;
    @GetMapping("/blog")
    /**
     * * Muestra la vista del blog
     * @param model
     * @param session
     * @return
     */
    public String blog(Model model, HttpSession session) {
            UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
        model.addAttribute("barrios", barriosRepository.findAll());
        model.addAttribute("usuario",usuario);
        return "blog";
    }

    @GetMapping("/privacidad")
    /**
     * * Muestra la vista de política de privacidad
     * @param model
     * @param session
     * @return
     */
    public String politicaPrivacidad(Model model, HttpSession session) {
         UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
          model.addAttribute("barrios", barriosRepository.findAll());
        model.addAttribute("usuario",usuario);
        return "privacidad";
    }

    @GetMapping("/condiciones")
    /**
     * * Muestra la vista de términos y condiciones
     * @param model
     * @param session
     * @return
     */
    public String terminosCondiciones(Model model, HttpSession session) {
         UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuario");
           model.addAttribute("barrios", barriosRepository.findAll());
        model.addAttribute("usuario", usuario);
        return "terminos";
    }
}


