/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.controller;

import cat.copernic.ranare.entity.mysql.Vehicle;
import cat.copernic.ranare.service.mysql.AdminVehicleService;
import com.mongodb.client.gridfs.model.GridFSFile;
import jakarta.annotation.Resource;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author ngall
 */

@Controller
public class ControladorAdminVehicle {
    
    @Autowired
    private AdminVehicleService adminVehicleService;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private GridFsTemplate gridFsTemplate;
    
    @GetMapping("/admin/vehicles")
    public String mostrarVehicles(Model model){
        List<Vehicle> vehicles = adminVehicleService.getAllVehicles();
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("title", "Vehicles");
        model.addAttribute("content", "admin-vehicles :: vehicleContent");
        return "admin";
    }
    
    @GetMapping("/admin/seleccionarVehicle")
    public String seleccionarVehicle(@RequestParam(name = "matricula", required = false) 
            String matricula, RedirectAttributes redirectAttributes){
        if(matricula != null && !matricula.isEmpty()){
            return "redirect:/crear-vehicle?matricula=" + matricula;
        }else{
            return "redirect:/crear-vehicle";
        }
    }
   
    @PostMapping("/admin/eliminarVehicle")
    public String eliminarVehicle(@RequestParam(name = "matriculas", required = false)
            List<String> matriculas, RedirectAttributes redirectAttributes){
        if(matriculas != null && !matriculas.isEmpty()){
            adminVehicleService.deleteVehiclesByIds(matriculas);
            redirectAttributes.addFlashAttribute("message", "Vehicle eliminat correctament.");
        }else{
            redirectAttributes.addFlashAttribute("errorMessage","No s'ha seleccionat cap vehicle per eliminar.");
        }
        return "redirect:/admin/vehicles";
    }
    
    @GetMapping("/admin/vehicle/imatge/{matricula}")
    public ResponseEntity<ByteArrayResource> obtenirImatgeVehicle(@PathVariable String matricula){
        Vehicle vehicle = adminVehicleService.getVehicleByMatricula(matricula);
        if(vehicle != null && vehicle.getImatgeVehicle() != null){
            ByteArrayResource imatge = new ByteArrayResource(vehicle.getImatgeVehicle());
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imatge);
        }else{
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/admin/vehicles/pdf/{id}")
    public ResponseEntity<ByteArrayResource> obtenirPDF(@PathVariable String id){
        try{
            ObjectId objectId = new ObjectId(id);
            
            GridFSFile gridFSFile = gridFsTemplate.findOne(
                    org.springframework.data.mongodb.core.query.Query.query(
                        org.springframework.data.mongodb.core.query.Criteria.where("_id").is(objectId))
            );
            
            if(gridFSFile != null){
                GridFsResource resource = gridFsTemplate.getResource(gridFSFile);
                
                byte[] pdfContent = resource.getInputStream().readAllBytes();
                ByteArrayResource byteResource = new ByteArrayResource(pdfContent);
                
                return ResponseEntity.ok()
                        .contentLength(pdfContent.length)
                        .contentType(MediaType.APPLICATION_PDF)
                        .header("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(byteResource);
            }else {
                return ResponseEntity.notFound().build();
            }
        }catch (IllegalArgumentException e) {
        // Si el ID no es válido
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
        // Error interno del servidor
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}