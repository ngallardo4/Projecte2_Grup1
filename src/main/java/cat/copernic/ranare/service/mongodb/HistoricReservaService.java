/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.service.mongodb;

import cat.copernic.ranare.entity.mongodb.HistoricReserva;
import cat.copernic.ranare.entity.mysql.Reserva;
import cat.copernic.ranare.repository.mongodb.HistoricReservaRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Raú
 */
@Service
public class HistoricReservaService {

    @Autowired
    private HistoricReservaRepository historicReservaRepository;

    // Método para obtener el histórico
    public List<HistoricReserva> obtenerHistoricPorIdReserva(String idReserva) {
    return historicReservaRepository.findByIdReserva(idReserva);
}
    
    

    // Método para registrar en el histórico
    // Método para registrar en el histórico
    public void registrarEnHistoric(Reserva reserva, String accio) {
        HistoricReserva historic = new HistoricReserva();
        historic.setIdReserva(reserva.getId().toString());
        historic.setAccio(accio);
        historic.setEstat(reserva.getEstat().name());
        historic.setDataInici(reserva.getDataInici());
        historic.setDataFin(reserva.getDataFin());
        historic.setCostReserva(reserva.getCostReserva());
        historic.setFianca(reserva.getFianca());
        historic.setTimestamp(LocalDateTime.now());

        // Obtener el DNI del cliente y la matrícula del vehículo
        String dniCliente = reserva.getClient().getDni(); // Asumiendo que el cliente tiene el atributo dni
        String matriculaVehiculo = reserva.getVehicle().getMatricula(); // La matrícula del vehículo

        // Establecer solo el DNI del cliente y la matrícula del vehículo
        historic.setDniCliente(dniCliente);
        historic.setMatriculaVehiculo(matriculaVehiculo);

        // Otros campos si son necesarios
        // Guardar el histórico en la base de datos
        historicReservaRepository.save(historic);
    }


    
    
    public List<HistoricReserva> getAllHistoricReserva() {
        return historicReservaRepository.findAll();
    }


}
