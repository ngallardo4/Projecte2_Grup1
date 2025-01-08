/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.controller;

import cat.copernic.ranare.entity.mongodb.HistoricReserva;
import cat.copernic.ranare.entity.mysql.Client;
import cat.copernic.ranare.entity.mysql.Reserva;
import cat.copernic.ranare.entity.mysql.Vehicle;
import cat.copernic.ranare.entity.mysql.VehicleDTO;
import cat.copernic.ranare.service.mongodb.HistoricReservaService;

import cat.copernic.ranare.service.mysql.ClientService;
import cat.copernic.ranare.service.mysql.ReservaService;
import cat.copernic.ranare.service.mysql.VehicleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Classe que gestiona les funcionalitats relacionades amb les reserves del
 * sistema. Aquesta classe és un controlador que combina la gestió del frontend
 * amb Thymeleaf i els serveis REST necessaris per a comunicació asíncrona amb
 * el backend.
 *
 * Modificacions recents: - Implementació de càlcul de la fiança i desglòs de
 * costos. - Gestió de vehicles disponibles segons el període de reserva. -
 * Optimització del procés de reserva per evitar conflictes en les dates.
 *
 * @author Raú
 */
@Controller
@RequestMapping("/admin/reserves")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private HistoricReservaService historicReservaService;

    /**
     * Mostra el formulari per crear una nova reserva.
     *
     * Aquest mètode inicialitza el formulari per la creació d'una reserva.
     * Carrega la llista de clients i vehicles disponibles per ser seleccionats.
     *
     * @param model Objecte del model utilitzat per passar dades a la vista.
     * @return El nom de la plantilla Thymeleaf "crear_reserva".
     */
    @GetMapping("/nova")
    public String mostrarFormulariNovaReserva(Model model) {
        model.addAttribute("reserva", new Reserva());
        model.addAttribute("clients", clientService.getOnlyClients());
        model.addAttribute("vehicles", vehicleService.getAllVehicles());
        model.addAttribute("title", "Crear Reserva");
        model.addAttribute("content", "crear_reserva :: crearReservaContent");
        return "admin";
    }

    /**
     * Gestiona l'enviament del formulari per crear una nova reserva.
     *
     * Aquest mètode valida les dades del formulari, calcula la fiança i el cost
     * total de la reserva segons les dades seleccionades i guarda la reserva al
     * sistema.
     *
     * @param reserva L'objecte Reserva amb les dades del formulari.
     * @param result Resultat de la validació del formulari.
     * @param redirectAttributes Objecte per afegir missatges d'estat a les
     * redireccions.
     * @param model
     * @return Redirecció a la pàgina de llista de reserves.
     */
    @PostMapping("/crear")
    public String crearReserva(
            @ModelAttribute @Valid Reserva reserva, BindingResult result,
            RedirectAttributes redirectAttributes, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("title", "Crear Reserva");
            model.addAttribute("content", "crear_reserva :: crearReservaContent");
            return "admin";
        }

        // Validar cliente y vehículo
        Client client = clientService.getClientById(reserva.getClient().getDni())
                .orElseThrow(() -> new IllegalArgumentException("El client no existeix."));
        Vehicle vehicle = vehicleService.getVehicleByMatricula(reserva.getVehicle().getMatricula());

        // Validar disponibilitat del vehicle
        if (!vehicle.isDisponibilitat()) {
            redirectAttributes.addFlashAttribute("error", "El vehicle no està disponible.");
            return "redirect:/admin/reserves/nova";
        }

        // Validar durada
        LocalDateTime dataInici = reserva.getDataInici();
        LocalDateTime dataFin = reserva.getDataFin();
        long hores = ChronoUnit.HOURS.between(dataInici, dataFin);

        if (hores < vehicle.getMinimHoresLloguer() || hores > vehicle.getMaximHoresLloguer()) {
            redirectAttributes.addFlashAttribute("error", "La duració de la reserva no compleix els requisits del vehicle.");
            return "redirect:/admin/reserves/nova";
        }

        // Calcular costos
        double fianca = reservaService.calcularFianca(client, vehicle);
        double costReserva = reservaService.calcularCostReserva(dataInici, dataFin, vehicle.getPreuPerHoraLloguer(), fianca);

        reserva.setFianca(fianca);
        reserva.setCostReserva(costReserva);

        try {
            reservaService.crearReserva(reserva); // Este método debe registrar en el histórico
            redirectAttributes.addFlashAttribute("missatge", "Reserva creada correctament.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/reserves/nova";
        }
        return "redirect:/admin/reserves";
    }

    /**
     * Mostra la llista de totes les reserves.
     *
     * Aquest mètode carrega totes les reserves del sistema i les passa a la
     * vista.
     *
     * @param query
     * @param model Objecte del model utilitzat per passar dades a la vista.
     * @return El nom de la plantilla Thymeleaf "llista_reserves".
     */
    @GetMapping
    public String llistarReserves(@RequestParam(value = "query", required = false) String query, Model model) {
        List<Reserva> reserves;

        if (query != null && !query.isEmpty()) {
            reserves = reservaService.buscarReservas(query);
        } else {
            reserves = reservaService.getAllReserves();
        }

        double sumaTotalCost = reserves.stream()
                .mapToDouble(Reserva::getCostReserva)
                .sum();

        model.addAttribute("reserves", reserves);
        model.addAttribute("totalReserves", reserves.size());
        model.addAttribute("sumaTotalCost", sumaTotalCost);
        model.addAttribute("isAdmin", true); // Este método corresponde al admin
        model.addAttribute("title", "Llista de reserves");
        model.addAttribute("content", "llista_reserves :: llistarReservaContent");

        return "admin";
    }

    /**
     * Gestiona la sol·licitud per anul·lar una reserva existent.
     *
     * Aquest mètode actualitza l'estat de la reserva a "anul·lada".
     *
     * @param id Identificador de la reserva que s'ha d'anul·lar.
     * @param redirectAttributes Objecte per afegir missatges d'estat a les
     * redireccions.
     * @return Redirecció a la pàgina de llista de reserves.
     */
    @PostMapping("/anular")
    public String anularReserva(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        reservaService.anularReserva(id);
        redirectAttributes.addFlashAttribute("missatge", "Reserva anul·lada correctament.");
        return "redirect:/admin/reserves";
    }

    @GetMapping("/filtrar-vehiculos")
    @ResponseBody
    public List<VehicleDTO> filtrarVehiculosDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInici,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFin) {

        // Obtenir vehicles disponibles per al període
        List<VehicleDTO> vehiclesDTO = (List<VehicleDTO>) vehicleService.filtrarVehiculosDisponiblesDTO(dataInici, dataFin, false);
        return vehiclesDTO;
    }

    @GetMapping("/detall/{id}")
    public String detallsReserva(@PathVariable Long id, Model model, HttpServletRequest request) {
        Optional<Reserva> reservaOpt = reservaService.obtenirReservaPerId(id);
        if (reservaOpt.isEmpty()) {
            return "error"; // Redirige a una página de error si la reserva no existe
        }
        Reserva reserva = reservaOpt.get();

        // Comprueba si client y vehicle no son nulos
        if (reserva.getClient() == null || reserva.getVehicle() == null) {
            System.out.println("Cliente está vacío o el vehículo no está definido");
        }

        // Determina si es un administrador según la URL
        boolean isAdmin = request.getRequestURI().contains("/admin");

        model.addAttribute("reserva", reserva);
        model.addAttribute("title", "Detall reserva");
        model.addAttribute("content", "detalls_reserva :: detallReservaContent");
        model.addAttribute("isAdmin", isAdmin);

        return "admin";
    }

    @GetMapping("/buscar")
public String buscarReservas(@RequestParam(value = "query", required = false) String query, Model model) {
    List<Reserva> reservasFiltradas;

    if (query != null && !query.isEmpty()) {
        reservasFiltradas = reservaService.buscarReservas(query);
    } else {
        reservasFiltradas = reservaService.getAllReserves();
    }

    model.addAttribute("reserves", reservasFiltradas);
    model.addAttribute("totalReserves", reservasFiltradas.size());
    model.addAttribute("isAdmin", true); // Asegúrate de agregar siempre esta variable
    model.addAttribute("title", "Llista de reserves");
    model.addAttribute("content", "llista_reserves :: llistarReservaContent");

    return "admin";
}

    @PostMapping("/{id}/lliurament")
    public String marcarLliurament(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservaService.marcarLliurament(id);
            redirectAttributes.addFlashAttribute("missatge", "El lliurament s'ha marcat correctament.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/reserves/detall/" + id;
    }

    @PostMapping("/{id}/devolucio")
    public String marcarDevolucio(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservaService.marcarDevolucio(id);
            redirectAttributes.addFlashAttribute("missatge", "La devolució s'ha marcat correctament.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/reserves/detall/" + id;
    }

    @GetMapping("/historic")
    public String mostrarHistoric(Model model) {
        List<HistoricReserva> historic = historicReservaService.getAllHistoricReserva();
        model.addAttribute("historic", historic);
        return "historic_reserva"; // Nombre del archivo HTML Thymeleaf
    }

    @GetMapping("/historic/{id}")
    public String veureHistoricPerReserva(@PathVariable("id") String idReserva, Model model) {
        List<HistoricReserva> historic = historicReservaService.obtenerHistoricPorIdReserva(idReserva);
        if (historic == null || historic.isEmpty()) {
            throw new UnsupportedOperationException("No hi ha dades històriques disponibles.");
        }
        model.addAttribute("historic", historic);
        model.addAttribute("idReserva", idReserva); // Mostrar en la vista
        return "historic_reserva";
    }

    @GetMapping("/buscar-per-id")
public String buscarReservaPorId(@RequestParam(value = "id", required = false) Long id, Model model) {
    List<Reserva> reservasFiltradas;

    if (id != null) {
        Optional<Reserva> reservaOpt = reservaService.obtenirReservaPerId(id);
        reservasFiltradas = reservaOpt.map(Collections::singletonList).orElse(Collections.emptyList());
    } else {
        reservasFiltradas = reservaService.getAllReserves();
    }

    model.addAttribute("reserves", reservasFiltradas);
    model.addAttribute("totalReserves", reservasFiltradas.size());
    model.addAttribute("isAdmin", true); // Asegúrate de agregar siempre esta variable
    model.addAttribute("title", "Llista de reserves");
    model.addAttribute("content", "llista_reserves :: llistarReservaContent");

    return "admin";
}

}
