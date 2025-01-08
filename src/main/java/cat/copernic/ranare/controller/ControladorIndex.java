/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author reyes
 */
@Controller
public class ControladorIndex {

    @GetMapping("/")
    public String index(Model model) {
        // Puedes añadir variables al modelo aquí si necesitas pasar datos al HTML
        return "index"; // Renderiza la plantilla `index.html`
    }
}
