/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.controller;

import cat.copernic.ranare.entity.mysql.Localitzacio;
import cat.copernic.ranare.entity.mysql.Vehicle;
import cat.copernic.ranare.service.mysql.LocalitzacioService;
import cat.copernic.ranare.service.mysql.VehicleService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author ngall
 */
@Controller
@RequestMapping("/admin/vehicles")
public class ControladorCrearVehicle {

    @Autowired
    VehicleService vehicleService;

    @Autowired
    LocalitzacioService localitzacioService;

    @Autowired
    GridFsTemplate gridFsTemplate;

    @GetMapping("/crear-vehicle")
    public String mostrarFormulari(@RequestParam(name = "matricula", required = false) String matricula, Model model) {
        List<Localitzacio> localitzacions = localitzacioService.getallLocalitzacio();
        model.addAttribute("localitzacions", localitzacions);
        if (matricula != null && !matricula.isEmpty()) {
            Vehicle vehicle = vehicleService.getVehicleByMatricula(matricula);
            if (vehicle != null) {
                model.addAttribute("vehicle", vehicle);
                model.addAttribute("title", "Modificar Vehicle");
            } else {
                model.addAttribute("errorMessage", "Vehicle no trobat.");
            }
        } else {
            model.addAttribute("vehicle", new Vehicle());
            model.addAttribute("title", "Crear Vehicle");
        }
        model.addAttribute("content", "crear-vehicle :: crearVehicleContent");

        return "admin";
    }

    @PostMapping("/crear-vehicle")
    public String crearVehicle(@Valid Vehicle vehicle, BindingResult result, 
            RedirectAttributes redirectAttributes, Model model, 
            @RequestParam(value = "imatge", required = false) MultipartFile imatgeFile, 
            @RequestParam(value = "document", required = false) MultipartFile documentFile) {
        
        if (result.hasErrors()) {
            model.addAttribute("content", "crear-vehicle :: crearVehicleContent");
            return "admin";
        }
        
        try{
            Vehicle existingVehicle = null;
            
            if(vehicle.getMatricula() != null){
                existingVehicle = vehicleService.getVehicleByMatricula(vehicle.getMatricula());
            }
            
            if(imatgeFile != null && !imatgeFile.isEmpty()){
                vehicle.setImatgeVehicle(imatgeFile.getBytes());
            }else if(existingVehicle != null){
                vehicle.setImatgeVehicle(existingVehicle.getImatgeVehicle());
            }
            
            if(documentFile != null && !documentFile.isEmpty()){
                String fileName = StringUtils.cleanPath(documentFile.getOriginalFilename());
                ObjectId pdfId = gridFsTemplate.store(documentFile.getInputStream(), fileName, documentFile.getContentType());
                vehicle.setPdfId(pdfId.toString());
            }else if(existingVehicle != null){
                vehicle.setPdfId(existingVehicle.getPdfId());
            }
            
            vehicleService.saveVehicle(vehicle);
            redirectAttributes.addFlashAttribute("message", "El vehicle s'ha creat o modificat correctament.");
            return "redirect:/admin/vehicles/crear-vehicle";
        }catch (IOException e){
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error en carregar els fitxers. Si us plau, intenta-ho de nou.");
            model.addAttribute("content", "crear-vehicle :: crearVehicleContent");
            return "admin";
        } 
    }

    @PostMapping("/pujarImatge")
    public String pujarImatge(@RequestParam("image") MultipartFile file, Model model) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        ObjectId fileId = gridFsTemplate.store(file.getInputStream(), fileName, file.getContentType());

        model.addAttribute("message", "Imatge penjada correctament!");
        model.addAttribute("content", "crear-vehicle :: crearVehicleContent");
        return "admin";
    }
}
