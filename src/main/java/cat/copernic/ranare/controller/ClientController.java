/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.controller;

import cat.copernic.ranare.entity.mongodb.DocumentacioUsuari;
import cat.copernic.ranare.entity.mysql.Client;
import cat.copernic.ranare.enums.DocumentType;
import cat.copernic.ranare.enums.Rol;
import cat.copernic.ranare.exceptions.ClientNotFoundException;

import cat.copernic.ranare.service.mysql.ClientService;
import cat.copernic.ranare.exceptions.DuplicateResourceException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import cat.copernic.ranare.response.ErrorResponse;
import cat.copernic.ranare.service.mongodb.DocumentacioUsuariService;
import java.util.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador per gestionar les operacions relacionades amb els clients.
 *
 * Aquest controlador inclou operacions per crear, modificar, eliminar i
 * consultar clients tant en vistes HTML com en API REST.
 *
 * També afegeix suport per a la creació d'agents per part d'usuaris
 * administradors.
 *
 * @author Raú
 */
@Controller
@RequestMapping("/admin/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private DocumentacioUsuariService documentacioUsuariService;

    /**
     * Processa el formulari de creació de clients o agents. Gestiona errors de
     * duplicació.
     *
     * @param client El client creat a partir del formulari.
     * @param bindingResult El resultat de la validació del formulari.
     * @return La redirecció a la pàgina de llista de clients si és correcte, o
     * el formulari si hi ha errors.
     */
    @PostMapping("/api")
    public ResponseEntity<Object> createOrUpdateClient(@RequestBody @Valid Client client, BindingResult bindingResult) {
        // Si hay errores de validación en el formulario
        if (bindingResult.hasErrors()) {
            // Creamos una respuesta de error con los errores de validación
            ErrorResponse errorResponse = new ErrorResponse(bindingResult.getAllErrors());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        try {
            // Guardar o actualizar el cliente
            Client savedClient = clientService.saveClient(client, true, bindingResult, true);

            // Si no hay errores y el cliente se guarda correctamente
            return new ResponseEntity<>(savedClient, HttpStatus.CREATED);
        } catch (DuplicateResourceException e) {
            // Si se lanza una excepción por duplicado, devolver un error con el mensaje adecuado
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Obtenir un client pel seu DNI a través d'una API REST.
     *
     * @param dni El DNI del client que volem obtenir.
     * @return La resposta amb el client si existeix, o un error 404 si no.
     */
    @GetMapping("/api/{dni}")
    public ResponseEntity<Client> getClientById(@PathVariable String dni) {
        Optional<Client> client = clientService.getClientById(dni);
        return client.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Obtenir tots els clients a través d'una API REST.
     *
     * @return La llista de tots els clients.
     */
    @GetMapping("/api")
    public List<Client> getAllClients() {
        return clientService.getAllClients();
    }

    /**
     * Mostrar la llista de clients en una vista HTML.
     *
     * @param filtro
     * @param model El model per passar la llista de clients a la vista.
     * @return El nom de la plantilla Thymeleaf per mostrar la llista de
     * clients.
     */
    @GetMapping
    public String showClientList(
            @RequestParam(value = "filtro", required = false) String filtro,
            Model model) {

        List<Client> clients;
        if (filtro != null && !filtro.isEmpty()) {
            clients = clientService.searchClients(filtro);
        } else {
            clients = clientService.getOnlyClients();
        }

        model.addAttribute("clients", clients);
        model.addAttribute("filtro", filtro);
        model.addAttribute("title", "Llista clients");
        model.addAttribute("content", "llista_de_clients :: llistaClientsContent");
        return "admin";
    }

    /**
     * Mostra el formulari per crear un nou client.
     *
     * Inclou comprovació del rol de l'usuari autenticat per determinar si pot
     * crear agents.
     *
     * @param model El model per passar un client buit a la vista.
     * @param loggedUser L'usuari autenticat per comprovar permisos.
     * @return El nom de la plantilla Thymeleaf per crear un client.
     */
    @GetMapping("/crear_client")
    public String showForm(Model model) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        boolean isAdmin = false;
        if(principal instanceof User){
            User loggedUser = (User)principal;
            isAdmin = loggedUser.getAuthorities().stream().anyMatch(auth ->
            auth.getAuthority().equals("ROLE_ADMIN"));
        }
        model.addAttribute("isAdmin", isAdmin);

        model.addAttribute("client", new Client());
        model.addAttribute("rols", Rol.values()); // Añadir roles disponibles (AGENT, ADMIN)
        model.addAttribute("title", "Crear client");
        model.addAttribute("content", "crear_client :: crearClientsContent");
        return "admin"; // Plantilla Thymeleaf para el formulario de creación
    }

    /*
    /**
     * Procesa el formulario de creación de clientes o agentes. Gestiona errores
     * de duplicación.
     *
     * @param client El cliente creado a partir del formulario.
     * @param rol
     * @param bindingResult El resultado de la validación del formulario.
     * @param redirectAttributes Atributos de redirección para pasar mensajes de
     * éxito.
     * @param loggedUser
     * @return La redirección a la página de lista de clientes si es correcto, o
     * el formulario si hay errores.
     */
    @PostMapping("/crear_client")
    public String createClient(@ModelAttribute @Valid Client client,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            @RequestParam(required = false) Rol rol,
            @AuthenticationPrincipal User loggedUser, Model model) {

        // Si hay errores de validación, regresa al formulario
        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "Crear client");
            model.addAttribute("content", "crear_client :: crearClientsContent");
            return "admin";  // Vuelve al formulario de cliente si hay errores
        }

        // Convertir el DNI a mayúsculas
        if (client.getDni() != null) {
            client.setDni(client.getDni().toUpperCase());
        }

        try {
            // Guardar el cliente
            Client savedClient = clientService.saveClient(client, false, bindingResult, true);

            if (savedClient == null || bindingResult.hasErrors()) {
                model.addAttribute("title", "Crear client");
                model.addAttribute("content", "crear_client :: crearClientsContent");
                return "admin";  // Si hay errores de duplicados, vuelve al formulario
            }

            // Si el rol es "AGENT" y el usuario es administrador, redirige al formulario de agente
            if (loggedUser.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")) && rol != null && rol == Rol.AGENT) {
                // Crear un agente con los datos del cliente y redirigir al formulario de agente
                redirectAttributes.addFlashAttribute("success", "Agent creat correctament.");
                return "redirect:/admin/agents/crear-agent";  // Redirige al formulario de agente
            } else {
                // Si es un cliente normal
                redirectAttributes.addFlashAttribute("missatge", "Cliente creat correctament.");
                return "redirect:/admin/clients";  // Redirige a la lista de clientes
            }

        } catch (DuplicateResourceException e) {
            // Manejar errores de duplicados
            if (e.getMessage().contains("DNI")) {
                bindingResult.rejectValue("dni", "duplicate.dni", e.getMessage());
            }
            if (e.getMessage().contains("email")) {
                bindingResult.rejectValue("email", "duplicate.email", e.getMessage());
            }
            if (e.getMessage().contains("username)")) {
                bindingResult.rejectValue("username", "duplicate.username", e.getMessage());
            }
            model.addAttribute("title", "Crear client");
            model.addAttribute("content", "crear_client :: crearClientsContent");
            return "admin";  // Vuelve al formulario si hay errores
        }
    }

    /**
     * Mostrar el formulari per modificar un client.
     *
     * @param dni El DNI del client que es vol modificar.
     * @param model El model per passar les dades del client a la vista.
     * @return El nom de la plantilla Thymeleaf per editar el client, o la
     * redirecció a la llista de clients si no es troba.
     */
    @GetMapping("/modificar/{dni}")
    public String showEditForm(@PathVariable String dni, Model model) {
        Optional<Client> clientOpt = clientService.getClientById(dni);
        if (clientOpt.isPresent()) {
            model.addAttribute("client", clientOpt.get());
            model.addAttribute("title", "Modificar client");
            model.addAttribute("content", "modificar_client :: modificarClientsContent");
            return "admin"; // Plantilla Thymeleaf per editar un client
        }
        return "redirect:/admin/clients"; // Redirigeix a la llista si no es troba el client
    }

    /**
     * Procesa la modificación de un cliente. Gestiona errores de duplicación.
     *
     * @param dni El DNI del cliente que se quiere modificar.
     * @param client Los nuevos datos del cliente.
     * @param result El resultado de la validación del formulario.
     * @param redirectAttributes Atributos de redirección para pasar mensajes de
     * éxito.
     * @return La redirección a la página de lista de clientes si es correcto, o
     * el formulario si hay errores.
     */
    @PostMapping("/modificar/{dni}")
    public String updateClient(@PathVariable String dni,
            @ModelAttribute @Valid Client client,
            BindingResult result,
            RedirectAttributes redirectAttributes, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("title", "Modificar client");
            model.addAttribute("content", "modificar_client :: modificarClientsContent");
            return "admin"; // Si hay errores de validación, vuelve al formulario
        }

        try {
            // Asegúrate de que el DNI no cambia durante la actualización
            client.setDni(dni);
            Client updatedClient = clientService.saveClient(client, true, result, true);
            if (updatedClient == null) {
                model.addAttribute("title", "Modificar client");
                model.addAttribute("content", "modificar_client :: modificarClientsContent");
                return "admin"; // Si hubo errores de duplicado, vuelve al formulario
            }

            redirectAttributes.addFlashAttribute("successMessage", "El client s'ha modificat correctament.");
        } catch (DuplicateResourceException e) {
            // Manejo de la excepción por duplicado de DNI o email
            if (e.getMessage().contains("DNI")) {
                result.rejectValue("dni", "duplicate.dni", e.getMessage());
            }
            if (e.getMessage().contains("correu electrònic")) {
                result.rejectValue("email", "duplicate.email", e.getMessage());
            }

            // Retorna al formulario con los errores
            model.addAttribute("title", "Modificar client");
            model.addAttribute("content", "modificar_client :: modificarClientsContent");
            return "admin"; // Vuelve al formulario con los errores
        }

        return "redirect:/admin/clients"; // Redirige a la lista de clientes si no hay errores
    }

    /**
     * Eliminar un client pel seu DNI.
     *
     * @param dni El DNI del client que volem eliminar.
     * @param redirectAttributes
     * @return Una redirecció a la llista de clients després d'eliminar el
     * client.
     */
    @PostMapping("/eliminar_client")
    public String eliminarClient(@RequestParam("dni") String dni, RedirectAttributes redirectAttributes) {
        try {
            clientService.deleteClient(dni);
            redirectAttributes.addFlashAttribute("success", "Client amb dni: " + dni + " eliminat correctament");
        } catch (ClientNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "No s'ha pogut eliminar el client: " + e.getMessage());
        }

        return "redirect:/admin/clients";
    }

    /*
    @GetMapping("/detall/{dni}")
    public String veureDetallsClient(@PathVariable String dni, Model model) {
        Optional<Client> clientOpt = clientService.getClientById(dni);
        if (clientOpt.isPresent()) {
            model.addAttribute("client", clientOpt.get());
            model.addAttribute("title", "Detall client");
            model.addAttribute("content", "detalls_client :: detallClientsContent");
            return "admin";
        } else {
            model.addAttribute("missatge", "Client no trobat.");
            return "redirect:/admin/clients";
        }
    } */
    @GetMapping("/inactius")
    public String mostrarInactius(Model model) {
        model.addAttribute("clients", clientService.getInactiveClients());
        model.addAttribute("title", "Clients inactius");
        model.addAttribute("content", "llista-clients-inactius :: inactiusClientsContent");
        return "admin";
    }

    @PostMapping("/{dni}/activar")
    public String activarClient(@PathVariable("dni") String dni, RedirectAttributes redirectAttributes) {
        clientService.activarClient(dni);
        redirectAttributes.addFlashAttribute("successMessage", "El client amb DNI " + dni + " s'ha activat correctament.");
        return "redirect:/admin/clients/inactius";
    }

    /*
    @GetMapping("/detall/{dni}")
    public String veureDocumentacioClient(@PathVariable("dni") String dni, Model model) {
        // Log para identificar si el cliente existe
        System.out.println("Buscando cliente con DNI: " + dni);
        Optional<Client> client = clientService.getClientById(dni);

        if (!client.isPresent()) { // Corrige el chequeo de existencia
            System.out.println("Cliente no encontrado para el DNI: " + dni);
            return "error/404"; // Página de error si no existe
        }

        model.addAttribute("client", client.get());

        // Log para identificar documentos asociados
        System.out.println("Buscando documentación activa para el cliente: " + dni);
        List<DocumentacioUsuari> documents = documentacioUsuariService.obtenirDocumentsActiusPerUsuari(dni);

        if (documents.isEmpty()) {
            System.out.println("No se encontraron documentos activos para el cliente con DNI: " + dni);
        } else {
            documents.forEach(doc -> {
                System.out.println("Documento encontrado: " + doc.getDocumentType()
                        + ", Front MIME: " + doc.getFrontFileMimeType()
                        + ", Back MIME: " + doc.getBackFileMimeType());
            });
        }

        // Filtrar documentos según el tipo
        DocumentacioUsuari dniDocument = documents.stream()
                .filter(doc -> doc.getDocumentType() == DocumentType.DNI)
                .findFirst()
                .orElse(null);

        DocumentacioUsuari license = documents.stream()
                .filter(doc -> doc.getDocumentType() == DocumentType.LLICENCIA)
                .findFirst()
                .orElse(null);

        // Logs de documentos cargados
        if (dniDocument != null) {
            System.out.println("DNI Document cargado: " + dniDocument.getDocumentType());
        } else {
            System.out.println("No se encontró documentación de tipo DNI para el cliente.");
        }

        if (license != null) {
            System.out.println("Licencia cargada: " + license.getDocumentType());
        } else {
            System.out.println("No se encontró documentación de tipo LLICENCIA para el cliente.");
        }

        model.addAttribute("dniDocument", dniDocument);
        model.addAttribute("license", license);

        return "admin/detalls_client";
    }
     */
    @GetMapping("/{id}/documents/dni/new")
    public String afegirDocumentacioDNI(@PathVariable("id") String clientId, Model model) {
        Optional<Client> client = clientService.getClientById(clientId);
        if (client.isEmpty()) {
            model.addAttribute("error", "Client no trobat.");
            return "redirect:/admin/clients";
        }
        model.addAttribute("client", client.get());
        model.addAttribute("documentType", "DNI");
        model.addAttribute("content", "afegir_documentacio :: afegirDocumentacioClientContent");
        return "admin";  // Vista general
    }

    @GetMapping("/{id}/documents/license/new")
    public String afegirDocumentacioLlicencia(@PathVariable("id") String clientId, Model model) {
        // Obtener el cliente
        Optional<Client> client = clientService.getClientById(clientId);

        // Verificar si el cliente existe
        if (client.isEmpty()) {  // Cambiado a isEmpty() en lugar de client == null
            model.addAttribute("error", "Client no trobat.");
            return "redirect:/admin/clients";  // Redirige a la lista de clientes o a una página de error
        }

        model.addAttribute("client", client.get());  // .get() para acceder al cliente dentro del Optional
        model.addAttribute("documentType", "LLICENCIA");
        model.addAttribute("content", "afegir_documentacio :: afegirDocumentacioClientContent");
        return "admin";
    }

    @PostMapping("/{id}/documents/save")
    public String guardarNovaDocumentacio(
            @PathVariable("id") String clientId,
            @RequestParam("documentType") String documentType,
            @RequestParam("frontFile") MultipartFile frontFile,
            @RequestParam("backFile") MultipartFile backFile,
            RedirectAttributes redirectAttributes) {

        boolean documentSaved = false;
        try {
            // Intentamos guardar la documentación
            documentacioUsuariService.afegirDocument(clientId, documentType, frontFile, backFile);
            documentSaved = true; // Si no hay excepciones, consideramos que el documento se ha guardado correctamente
            redirectAttributes.addFlashAttribute("success", "Documentació guardada correctament.");
            System.out.println("Documento guardado correctamente.");
        } catch (Exception e) {
            // Si ocurre un error, capturamos la excepción
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            System.out.println("Error al guardar el documento: " + e.getMessage());
        }

        // Comprobamos si el documento se guardó correctamente
        if (!documentSaved) {
            redirectAttributes.addFlashAttribute("error", "No s'ha pogut guardar el document.");
            System.out.println("No se ha podido guardar el documento.");
        }

        // Este log te dirá a dónde llega el código
        System.out.println("Redirigiendo a la página de detalle.");

        return "redirect:/admin/clients/detall/" + clientId;
    }

    @GetMapping("/{id}/documents/historic")
    public String veureHistoricDocumentacio(@PathVariable("id") String userId, Model model) {
        try {
            model.addAttribute("documents", documentacioUsuariService.obtenirHistoricDocuments(userId));
            model.addAttribute("client", clientService.getClientById(userId));
        } catch (Exception e) {
            model.addAttribute("error", "Error al carregar l'històric de documentació.");
        }
        return "admin/historic_documents";
    }

    @GetMapping("/detall/{dni}")
    public String veureDetallsClient(@PathVariable("dni") String dni, Model model) {
        // Obtener cliente por DNI
        Optional<Client> clientOpt = clientService.getClientById(dni);
        if (!clientOpt.isPresent()) {
            System.out.println("Cliente no encontrado para el DNI: " + dni);
            return "error/404";
        }

        // Agregar cliente al modelo
        Client client = clientOpt.get();
        model.addAttribute("client", client);
        model.addAttribute("title", "Detalls del Client");

        // Obtener documentación asociada
        List<DocumentacioUsuari> documents = documentacioUsuariService.obtenirDocumentsActiusPerUsuari(dni);
        if (documents.isEmpty()) {
            System.out.println("No se encontraron documentos activos para el cliente con DNI: " + dni);
        } else {
            documents.forEach(doc -> {
                System.out.println("Documento encontrado: " + doc.getDocumentType()
                        + ", Front MIME: " + doc.getFrontFileMimeType()
                        + ", Back MIME: " + doc.getBackFileMimeType());
            });
        }

        // Filtrar documento tipo DNI
        DocumentacioUsuari dniDocument = documents.stream()
                .filter(doc -> doc.getDocumentType() == DocumentType.DNI)
                .findFirst()
                .orElse(null);
        if (dniDocument != null) {
            String dniFrontBase64 = Base64.getEncoder().encodeToString(dniDocument.getFrontFile());
            String dniBackBase64 = Base64.getEncoder().encodeToString(dniDocument.getBackFile());
            model.addAttribute("dniDocument", dniDocument);
            model.addAttribute("dniFrontBase64", dniFrontBase64);
            model.addAttribute("dniBackBase64", dniBackBase64);
        } else {
            System.out.println("No se encontró un documento tipo DNI.");
        }

        // Filtrar documento tipo LICENSE
        DocumentacioUsuari licenseDocument = documents.stream()
                .filter(doc -> doc.getDocumentType() == DocumentType.LLICENCIA)
                .findFirst()
                .orElse(null);
        if (licenseDocument != null) {
            String licenseFrontBase64 = Base64.getEncoder().encodeToString(licenseDocument.getFrontFile());
            String licenseBackBase64 = Base64.getEncoder().encodeToString(licenseDocument.getBackFile());
            model.addAttribute("licenseDocument", licenseDocument);
            model.addAttribute("licenseFrontBase64", licenseFrontBase64);
            model.addAttribute("licenseBackBase64", licenseBackBase64);
        } else {
            System.out.println("No se encontró un documento tipo LICENSE.");
        }

        // Agregar contenido para Thymeleaf
        model.addAttribute("content", "detalls_client :: detallClientsContent");

        return "admin";
    }

}
