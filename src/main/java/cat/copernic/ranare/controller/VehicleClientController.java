/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.controller;

import cat.copernic.ranare.entity.mysql.Client;
import cat.copernic.ranare.entity.mysql.Reserva;
import cat.copernic.ranare.entity.mysql.Vehicle;
import cat.copernic.ranare.entity.mysql.VehicleDto2;
import cat.copernic.ranare.exceptions.InvalidHorariException;
import cat.copernic.ranare.service.mysql.ClientService;
import cat.copernic.ranare.service.mysql.ReservaService;
import cat.copernic.ranare.service.mysql.VehicleService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 *
 * @author reyes
 */
@Controller
public class VehicleClientController {
    
    @Autowired
    private VehicleService vehicleService;
    
    @Autowired
    private ClientService clientService;
    
    @Autowired
    private ReservaService reservaService;
    
    @GetMapping("/public/vehicles/disponibles")
    public String filtrarVehiclesDisponibilitat(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInici,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFinal, Model model){
        try{
            vehicleService.validarDates(dataInici, dataFinal);
            
            List<VehicleDto2> vehiclesDisponibles = (List<VehicleDto2>)vehicleService.filtrarVehiculosDisponiblesDTO(dataInici, dataFinal, true);

            model.addAttribute("content", "llista-vehicles-disponibles :: vehiclesClientContent");
            model.addAttribute("vehiclesDisponibles", vehiclesDisponibles);
        
        }catch(InvalidHorariException e){
            model.addAttribute("error", e.getMessage());
            model.addAttribute("content", "seleccionar-dates :: datesClientContent");
            model.addAttribute("randomVehicles", vehicleService.getRandomVehicles());
            return "base-public";
        }
        
        return "base-public";
    }
    
    @GetMapping("/public/vehicles/reserva")
    public String realitzarReserva(@RequestParam("vehicleId") String matricula, 
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInici,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFinal, Model model){
        
        Vehicle vehicle = vehicleService.getVehicleByMatricula(matricula);
        String base64Img = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(vehicle.getImatgeVehicle());
        Client userLogejat = clientService.usuariLogejat();
        Reserva reserva = reservaService.prepararReserva(dataInici, dataFinal, matricula, userLogejat.getUsername());
        
        model.addAttribute("reserva", reserva);
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("img64", base64Img);
        model.addAttribute("user", userLogejat);
        model.addAttribute("startDateTime", dataInici);
        model.addAttribute("endDateTime", dataFinal);
        model.addAttribute("totalPrice", reservaService.calcularCostSenseFianca(dataInici, dataFinal, reserva.getVehicle().getPreuPerHoraLloguer()));
        model.addAttribute("deposit", reserva.getFianca());
        model.addAttribute("totalCost", reserva.getCostReserva());
        model.addAttribute("content", "reserva-client :: reservaClientContent");
        
        return "base-public";
    }
    
    @PostMapping("/public/vehicles/reserva/confirmar")
    public String confirmarReserva(@ModelAttribute @Valid Reserva reserva, BindingResult result, Model model){
        
        if(result.hasErrors()){
            model.addAttribute("error", "Error en la reserva.");
            model.addAttribute("content", "reserva-client :: reservaClientContent");
            return "base-public";
        }

        try{
            Reserva reservaGuardada = reservaService.crearReserva(reserva);
            //passem la ID de la reserva, que será el codi
            model.addAttribute("reservaId", reservaGuardada.getId());
            model.addAttribute("content", "confirmacio-reserva :: confirmarReservaContent");
            
            return "base-public";
        }catch(Exception e){
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}
