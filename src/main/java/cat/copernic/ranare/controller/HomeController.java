/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.controller;

import cat.copernic.ranare.entity.mysql.Vehicle;
import cat.copernic.ranare.service.mysql.VehicleService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author reyes
 */
@Controller
public class HomeController {
    
    @Autowired
    private VehicleService vehicleService;
    
    @GetMapping("/public")
    public String mostrarHome(Model model){
        //obtenim 3 vehicles random per mostrar per pantalla
        model.addAttribute("randomVehicles", vehicleService.getRandomVehicles());
        model.addAttribute("content", "seleccionar-dates :: datesClientContent");
        return "base-public";
    }
}
