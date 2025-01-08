/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.controller;

import cat.copernic.ranare.entity.mysql.Client;
import cat.copernic.ranare.service.mysql.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author reyes
 */
@Controller
@RequestMapping("/public/registre")
public class RegistreController {
    
    @Autowired
    ClientService clientService;
    
    @GetMapping("/pas1")
    public String showForm(Model model) {
        model.addAttribute("client", new Client());
        return "registre-client"; // Plantilla Thymeleaf para el formulario de creación
    }
    
    @PostMapping("/pas1")
    public String validarPas1(@Valid @ModelAttribute("client") Client client,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes, Model model){
        
        if (client.getDni() != null) {
            client.setDni(client.getDni().toUpperCase());
        }     
        Client savedClient = clientService.saveClient(client, false, bindingResult, false);
            
        if (savedClient == null || bindingResult.hasErrors()) {
            return "registre-client";  // Si hi ha errors, retorna al formulari
        }
            
        return "redirect:/public/registre/pas2";
    }
    
    @GetMapping("/pas2")
    public String showForm2(){
        return "registre-client-documents";
    }
    
    @PostMapping("/pas2")
    public String finalitzarRegistre(){
        return "redirect:/public/registre/success";
    }
    
    @GetMapping("/success")
    public String mostrarSuccess(){
        return "success";
    }
}
