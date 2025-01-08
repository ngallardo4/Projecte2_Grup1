/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.controller;

import cat.copernic.ranare.entity.mysql.Agent;
import cat.copernic.ranare.entity.mysql.Localitzacio;
import cat.copernic.ranare.entity.mysql.Vehicle;
import cat.copernic.ranare.exceptions.DuplicateResourceException;
import cat.copernic.ranare.exceptions.EntitatRelacionadaException;
import cat.copernic.ranare.exceptions.InvalidCodiPostalException;
import cat.copernic.ranare.exceptions.InvalidHorariException;
import cat.copernic.ranare.service.mysql.AgentService;
import cat.copernic.ranare.service.mysql.LocalitzacioService;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * Controlador per gestionar les operacions relacionades amb les localitzacions.
 * Aquest controlador permet visualitzar totes les localitzacions, accedir al formulari per crear-ne una nova
 * i processar el formulari per afegir una localització.
 *
 * @author reyes
 * @version 03/12/2024 v7
 */

@Controller
@RequestMapping("/admin/localitzacio")
public class LocalitzacioController {
    
    /**
     * Servei de localitzacions utilitzat per realitzar operacions sobre les dades de localitzacions.
     */
    @Autowired  
    private LocalitzacioService localitzacioService;
    
    @Autowired
    private AgentService agentService;
    
    Logger logger = LoggerFactory.getLogger(ClientController.class);
    
    /**
     * Mostra una llista amb totes les localitzacions disponibles.
     *
     * @param model Model utilitzat per afegir dades que es passaran a la vista.
     * @return El nom de la vista que mostra les localitzacions.
     */
    @GetMapping
    public String mostrarLocalitzacions(Model model) {
        List<Localitzacio> localitzacions = localitzacioService.getallLocalitzacio();
        model.addAttribute("localitzacions", localitzacions);
        model.addAttribute("title", "Localitzacions");
        model.addAttribute("content", "localitzacio :: localitzacioContent");
        return "admin"; 
    }
    
    /**
     * Mostra el formulari per crear una nova localització.
     *
     * @param model Model utilitzat per passar una instància buida de {@link Localitzacio} a la vista.
     * @return El nom de la vista del formulari per crear una localització.
     */
    @GetMapping("/crear-localitzacio")
    public String showCrearLocalitzacioPage(Model model){
        
        List<Agent> agents = agentService.getAllAgents();

        // Depuración: registra la lista de agentes en el log
        agents.forEach(agent -> logger.info("Agent: {}, DNI: {}", agent.getNom(), agent.getDni()));
        
        model.addAttribute("localitzacio", new Localitzacio());
        model.addAttribute("agents", agentService.getAllAgents());
        model.addAttribute("crear", true);//passem aquesta variable per indivar al HTML que es per crear i mostri "crear"
        model.addAttribute("title", "Crear localitzacio");
        model.addAttribute("content", "crear-localitzacio :: crearLocalitzacioContent");
        
        return "admin";
    }
    
    /**
     * Processa el formulari per crear una nova localització.
     *
     * @param local L'objecte {@link Localitzacio} omplert amb les dades introduïdes al formulari.
     * @param redirectAttributes Objecte per passar missatges entre peticions HTTP amb redireccions.
     * @param model Model utilitzat per passar missatges d'error a la vista en cas d'excepció.
     * @return Una redirecció a la pàgina de llista de localitzacions si es crea correctament, 
     * o el nom de la vista del formulari si hi ha errors.
     */
    
    @PostMapping("/crear-localitzacio")
    public String createLocalitzacio(@ModelAttribute Localitzacio local, RedirectAttributes redirectAttributes, Model model){
        
        try{
            //validar horaris
            localitzacioService.validarHorari(local.getHorariApertura(), local.getHorariTancament());
            localitzacioService.saveLocalitzacio(local);

            // Afegeix un missatge de confirmació utilitzant flash attributes.
            redirectAttributes.addFlashAttribute("success", "Localització afegida correctament!");

            return "redirect:/admin/localitzacio";
        
        }catch(InvalidCodiPostalException e){
            // Afegeix un missatge d'error al model en cas d'excepció.
            model.addAttribute("error_codi", e.getMessage());
            model.addAttribute("error", "Hi ha un error");
            model.addAttribute("title", "Crear localitzacio");
            model.addAttribute("agents", agentService.getAllAgents());
            model.addAttribute("crear", true);
            model.addAttribute("content", "crear-localitzacio :: crearLocalitzacioContent");
            return "admin";
        }catch(InvalidHorariException e){
            model.addAttribute("error_horari", e.getMessage());
            model.addAttribute("error", "Hi ha un error");
            model.addAttribute("title", "Crear localitzacio");
            model.addAttribute("agents", agentService.getAllAgents());
            model.addAttribute("crear", true);
            model.addAttribute("content", "crear-localitzacio :: crearLocalitzacioContent");
            return "admin";
        }catch(DuplicateResourceException e){
            model.addAttribute("error", e.getMessage());
            model.addAttribute("title", "Crear localitzacio");
            model.addAttribute("agents", agentService.getAllAgents());
            model.addAttribute("crear", true);
            model.addAttribute("content", "crear-localitzacio :: crearLocalitzacioContent");
            return "admin";
        }
    }
    
    @GetMapping("/{codiPostal}/vehicles")
    public String mostrarVehicles(@PathVariable String codiPostal, Model model){
        try{
            Set<Vehicle> vehicles = localitzacioService.getVehiclePerLocalitzacio(codiPostal);
            model.addAttribute("vehicles",vehicles);
            //poner llistar vehicles aquí con el content
            return "admin-vehicles";
        }catch(InvalidCodiPostalException e){
            model.addAttribute("errorMissatge", e.getMessage());
            return "error";
        }
    }
    
    @GetMapping("/{codiPostal}")
    public String detallLocalitzacio(@PathVariable String codiPostal, Model model){
        try{
            Localitzacio localitzacio = localitzacioService.getLocalitzacioPerCodiPostal(codiPostal);
            model.addAttribute("localitzacio",localitzacio);
            model.addAttribute("title", "Detall localitzacio");
            model.addAttribute("content", "detall-localitzacio :: detallLocalitzacioContent");

            return "admin";
        }catch(InvalidCodiPostalException e){
            model.addAttribute("errorMissatge", e.getMessage());
            return "error";
        }
    }
    
    @GetMapping("/{codiPostal}/modificar")
    public String mostrarModificarLocalitzacio(@PathVariable String codiPostal, Model model){
        Localitzacio localitzacio = localitzacioService.getLocalitzacioPerCodiPostal(codiPostal);
        logger.info("Valor de horariApertura antes de procesar: " + localitzacio.getHorariApertura());
        model.addAttribute("localitzacio",localitzacio);
        model.addAttribute("agents", agentService.getAllAgents());
        model.addAttribute("crear", false);
        model.addAttribute("title", "Modificar localitzacio");
        model.addAttribute("content", "crear-localitzacio :: crearLocalitzacioContent");
      
        return "admin";
    }
    
    @PostMapping("/{codiPostal}/modificar")
    public String modificarLocalitzacio(@PathVariable String codiPostal,@ModelAttribute Localitzacio localitzacio, RedirectAttributes redirectAttributes, Model model){
        try{
            localitzacioService.validarHorari(localitzacio.getHorariApertura(), localitzacio.getHorariTancament());
            localitzacioService.updateLocalitzacio(localitzacio);
            redirectAttributes.addFlashAttribute("success", "Localització modificada correctament!");
            return "redirect:/admin/localitzacio";
        }catch(InvalidCodiPostalException e){
           model.addAttribute("error_codi", e.getMessage());
            model.addAttribute("error", "Hi ha un error");
            model.addAttribute("title", "Modificar localitzacio");
            model.addAttribute("content", "crear-localitzacio :: crearLocalitzacioContent");
            return "admin";
        }catch(InvalidHorariException e){
            model.addAttribute("error_horari", e.getMessage());
            model.addAttribute("error", "Hi ha un error");
            model.addAttribute("title", "Modificar localitzacio");
            model.addAttribute("content", "crear-localitzacio :: crearLocalitzacioContent");
            return "admin";
        }
    }
    
    //eliminar localitzacio
    @PostMapping("/{codiPostal}/eliminar")
    public String eliminarLocalitzacio(@RequestParam("codiPostal") String codiPostal, RedirectAttributes redirectAttributes){
        try{
            localitzacioService.eliminarLocalitzacio(codiPostal);
            redirectAttributes.addFlashAttribute("success", "Localització amb codi postal: " + codiPostal +" eliminada correctament");
        }catch(InvalidCodiPostalException e){
            redirectAttributes.addFlashAttribute("error", "No s'ha pogut eliminar la localització: " + e.getMessage());
        }catch(EntitatRelacionadaException e){
            redirectAttributes.addFlashAttribute("error", "No s'ha pogut eliminar la localització: " + e.getMessage());
        }
        
        return "redirect:/admin/localitzacio";
    }
}
