/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.controller;

import cat.copernic.ranare.entity.mongodb.DocumentacioUsuari;
import cat.copernic.ranare.entity.mysql.Client;
import cat.copernic.ranare.enums.DocumentType;
import cat.copernic.ranare.service.mongodb.DocumentacioUsuariService;
import cat.copernic.ranare.service.mysql.ClientService;
import jakarta.validation.Valid;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;

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
 * @author Raú
 */
@Controller
@RequestMapping("/public/usuari")
public class UsuariController {

    @Autowired
    private ClientService clientService; // Servicio para manejar datos del cliente

    @Autowired
    private DocumentacioUsuariService documentacioService; // Servicio para manejar documentación

    @GetMapping("/detalls")
    public String veureDetallsUsuari(Model model) {
        // Obtener la autenticación del contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Verificar si el usuario está autenticado
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            System.out.println("[ERROR] Usuario no autenticado. Redirigiendo al login...");
            return "redirect:/public/login";
        }

        // Obtener el nombre de usuario
        String username = authentication.getName(); // Esto devuelve el username del usuario autenticado
        System.out.println("[INFO] Usuario autenticado: " + username);

        // Obtener los datos del cliente autenticado
        Optional<Client> clientOpt = clientService.findByUsername(username);
        if (clientOpt.isEmpty()) {
            System.out.println("[ERROR] No se encontró un cliente con el username: " + username);
            return "error/404";
        }

        Client client = clientOpt.get();
        System.out.println("[INFO] Cliente encontrado: " + client.getNom() + " " + client.getCognoms());

        // Agregar los datos del cliente al modelo
        model.addAttribute("user", client);

        // Obtener la documentación asociada al cliente
        List<DocumentacioUsuari> documents = documentacioService.obtenirDocumentsActiusPerUsuari(client.getDni());
        if (documents.isEmpty()) {
            System.out.println("[INFO] No se encontró documentación activa para el cliente con DNI: " + client.getDni());
        } else {
            documents.forEach(doc -> {
                System.out.println("[INFO] Documento encontrado: " + doc.getDocumentType()
                        + ", Front MIME: " + doc.getFrontFileMimeType());
            });
        }

        // Filtrar el documento tipo DNI
        DocumentacioUsuari dniDocument = documents.stream()
                .filter(doc -> doc.getDocumentType() == DocumentType.DNI)
                .findFirst()
                .orElse(null);

        if (dniDocument != null) {
            String dniFrontBase64 = Base64.getEncoder().encodeToString(dniDocument.getFrontFile());
            model.addAttribute("dniDocument", dniDocument);
            model.addAttribute("dniFrontBase64", dniFrontBase64);
            System.out.println("[INFO] Documento DNI cargado correctamente.");
        } else {
            System.out.println("[INFO] No se encontró un documento de tipo DNI.");
        }

        // Renderizar la vista del usuario
        return "usuari_detalls";
    }
    
    
    @PostMapping("/update")
    public String updateUsuari(@ModelAttribute @Valid Client client, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Hi ha errors en el formulari.");
            return "redirect:/public/usuari/detalls";
        }

        // Guardar los datos del usuario actualizado
        clientService.saveClient(client, true, result, false);
        redirectAttributes.addFlashAttribute("success", "Dades actualitzades correctament.");
        return "redirect:/public/usuari/detalls";
    }

    @PostMapping("/eliminar")
    public String eliminarUsuari(RedirectAttributes redirectAttributes) {
        // Obtener el usuario autenticado del SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            redirectAttributes.addFlashAttribute("error", "No s'ha pogut identificar l'usuari.");
            return "redirect:/public/login";
        }

        String username = authentication.getName();
        clientService.findByUsername(username);
        SecurityContextHolder.clearContext(); // Limpiar el contexto de seguridad
        redirectAttributes.addFlashAttribute("success", "Compte eliminat correctament.");
        return "redirect:/public/login";
    }

}
