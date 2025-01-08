 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.service.mysql;

import cat.copernic.ranare.entity.mysql.Client;
import cat.copernic.ranare.entity.mysql.Reserva;
import cat.copernic.ranare.entity.mysql.Vehicle;
import cat.copernic.ranare.enums.EstatReserva;
import cat.copernic.ranare.enums.Reputacio;
import cat.copernic.ranare.exceptions.ResourceNotFoundException;
import cat.copernic.ranare.repository.mysql.ClientRepository;
import cat.copernic.ranare.repository.mysql.ReservaRepository;
import cat.copernic.ranare.repository.mysql.VehicleRepository;
import cat.copernic.ranare.service.mongodb.HistoricReservaService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Raú
 */
@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;
    
    @Autowired 
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private HistoricReservaService historicReservaService;

    /**
     * Crea una nova reserva.
     *
     * @param reserva La reserva a guardar.
     * @return La reserva guardada.
     */
    public Reserva crearReserva(Reserva reserva) {
        // Asegúrate de que 'estat' no sea nulo
        if (reserva.getEstat() == null) {
            reserva.setEstat(EstatReserva.ACTIVA); // Asigna un valor predeterminado
        }

        // Guarda la reserva
        reserva = reservaRepository.save(reserva);

        // Registrar en el histórico después de guardar
        historicReservaService.registrarEnHistoric(reserva, "CREAR");

        return reserva;
    }

    /**
     * Obté una reserva pel seu identificador.
     *
     * @param id L'identificador de la reserva.
     * @return La reserva, si existeix.
     */
    public Optional<Reserva> obtenirReservaPerId(Long id) {
        return reservaRepository.findById(id);
    }

    /**
     * Obté totes les reserves d'un client.
     *
     * @param client El client.
     * @return Llista de reserves del client.
     */
    public List<Reserva> obtenirReservesPerClient(Client client) {
        return reservaRepository.findByClient(client);
    }

    /**
     * Anul·la una reserva canviant el seu estat.
     *
     * @param id L'identificador de la reserva.
     */
    public void anularReserva(Long id) {
        Optional<Reserva> reservaOpt = reservaRepository.findById(id);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            reserva.setEstat(EstatReserva.ANULADA);
            reservaRepository.save(reserva);
            historicReservaService.registrarEnHistoric(reserva, "ANULADA");
        }
    }

    /**
     * Finalitza una reserva canviant el seu estat.
     *
     * @param id L'identificador de la reserva.
     */
    public void finalitzarReserva(Long id) {
        Optional<Reserva> reservaOpt = reservaRepository.findById(id);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            reserva.setEstat(EstatReserva.FINALITZADA);
            reservaRepository.save(reserva);
        }
    }

    public List<Reserva> getAllReserves() {
        return reservaRepository.findAll();
    }

    public double calcularFianca(Client client, Vehicle vehicle) {
        double fiancaStandard = vehicle.getFiancaStandard();
        return client.getReputacio() == Reputacio.PREMIUM ? fiancaStandard * 0.75 : fiancaStandard;
    }
    
    public long calcularHores(LocalDateTime dataInici, LocalDateTime dataFin){
        return ChronoUnit.HOURS.between(dataInici, dataFin);
    }
    
    public double calcularCostSenseFianca(LocalDateTime dataInici, LocalDateTime dataFin, double preuPerHoraLloguer){
        long hores = calcularHores(dataInici, dataFin);
        if (hores <= 0) {
            throw new IllegalArgumentException("La data de finalització ha de ser com a mínim 1 hora posterior a la data d'inici.");
        }
        return (hores * preuPerHoraLloguer);
    }

    public double calcularCostReserva(LocalDateTime dataInici, LocalDateTime dataFin, double preuPerHoraLloguer, double fianca) {
        long hores = calcularHores(dataInici, dataFin);
        if (hores <= 0) {
            throw new IllegalArgumentException("La data de finalització ha de ser com a mínim 1 hora posterior a la data d'inici.");
        }
        return (hores * preuPerHoraLloguer) + fianca;
    }

    public List<Reserva> findOverlappingReservations(LocalDateTime dataInici, LocalDateTime dataFin) {
        EstatReserva estat = EstatReserva.ACTIVA; // Filtrar solo reservas activas
        return reservaRepository.findOverlappingReservations(dataInici, dataFin, estat);
    }

    public List<Reserva> buscarReservas(String query) {
        return reservaRepository.findReservasByQuery(query);
    }

    /**
     * Marca el lliurament de la reserva com a fet.
     *
     * @param id
     */
    public void marcarLliurament(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La reserva amb ID " + id + " no existeix."));

        if (!EstatReserva.ACTIVA.equals(reserva.getEstat())) {
            throw new IllegalArgumentException("Només es poden lliurar reserves actives.");
        }

        reserva.setLliurament(true);
        reservaRepository.save(reserva);
        historicReservaService.registrarEnHistoric(reserva, "LLIURAMENT");
    }

    public void marcarDevolucio(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La reserva amb ID " + id + " no existeix."));

        if (!reserva.isLliurament()) {
            throw new IllegalArgumentException("El vehicle no s'ha lliurat encara.");
        }

        reserva.setDevolucio(true);
        reserva.setEstat(EstatReserva.FINALITZADA);
        reservaRepository.save(reserva);
        historicReservaService.registrarEnHistoric(reserva, "DEVOLUCIO");
    }
    
    public Reserva prepararReserva(LocalDateTime dataInici, LocalDateTime dataFinal, String matricula, String username){
        Vehicle vehicle = vehicleRepository.findById(matricula)
            .orElseThrow(() -> new ResourceNotFoundException("Vehicle no trobat amb matricula: " + matricula));
        Client client = clientRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Client no trobat: " + username));
        
        //calcular preus
        double preuHora = vehicle.getPreuPerHoraLloguer();
        double fianca = calcularFianca(client, vehicle);
        double costReserva = calcularCostReserva(dataInici, dataFinal, preuHora, fianca);
        double costSenseFianca = calcularCostSenseFianca(dataInici, dataFinal, preuHora);
        
        //crear reserva
        Reserva reserva = new Reserva();
        reserva.setDataInici(dataInici);
        reserva.setDataFin(dataFinal);
        reserva.setVehicle(vehicle);
        reserva.setClient(client);
        reserva.setFianca(fianca);
        reserva.setCostReserva(costReserva);
        
        return reserva;
    }
    
    public List<Reserva> findByClientUsername(String username) {
        return reservaRepository.findByClientUsername(username);
    }
    
    
    

}
