/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.controller;

import cat.copernic.ranare.entity.mysql.Reserva;
import cat.copernic.ranare.service.mysql.ReservaService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/public/usuari/reserves")
public class UsuariReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping
    public String veureReservesUsuari(Model model, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/public/login";
        }

        String username = authentication.getName();
        List<Reserva> reserves = reservaService.findByClientUsername(username);

        // Verificar si la solicitud es desde /admin o /public
        boolean isAdmin = request.getRequestURI().contains("/admin");

        model.addAttribute("reserves", reserves);
        model.addAttribute("isAdmin", isAdmin);

        return "llista_reserves";
    }

    @GetMapping("/detall/{id}")
    public String veureDetallReserva(@PathVariable("id") Long reservaId, Model model, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/public/login";
        }

        String username = authentication.getName();
        Optional<Reserva> reservaOpt = reservaService.obtenirReservaPerId(reservaId);
        if (reservaOpt.isEmpty()) {
            return "error/404";
        }

        Reserva reserva = reservaOpt.get();
        if (!reserva.getClient().getUsername().equals(username)) {
            return "error/403";
        }

        // Verificar si la solicitud es desde /admin o /public
        boolean isAdmin = request.getRequestURI().contains("/admin");

        model.addAttribute("reserva", reserva);
        model.addAttribute("isAdmin", isAdmin);

        return "detalls_reserva";
    }
}
