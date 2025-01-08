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
public class LoginController {

    @GetMapping("/public/login")
    public String paginaLogin(Model model){
        model.addAttribute("content", "login :: loginContent");
        return "base-public";
    }
}
