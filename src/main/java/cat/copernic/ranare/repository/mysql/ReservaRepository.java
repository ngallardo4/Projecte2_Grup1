/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.repository.mysql;

import cat.copernic.ranare.entity.mysql.Client;
import cat.copernic.ranare.entity.mysql.Reserva;
import cat.copernic.ranare.entity.mysql.Vehicle;
import cat.copernic.ranare.enums.EstatReserva;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Raú
 */
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Override
    Optional<Reserva> findById(Long id);

    /**
     * Troba totes les reserves associades a un client específic.
     *
     * @param client El client de les reserves.
     * @return Llista de reserves del client.
     */
    List<Reserva> findByClient(Client client);

    /**
     * Troba totes les reserves associades a un vehicle específic.
     *
     * @param vehicle El vehicle de les reserves.
     * @return Llista de reserves del vehicle.
     */
    List<Reserva> findByVehicle(Vehicle vehicle);

    /**
     * Troba totes les reserves amb un estat específic.
     *
     * @param estat L'estat de les reserves.
     * @return Llista de reserves amb aquest estat.
     */
    List<Reserva> findByEstat(EstatReserva estat);

    /**
     *
     * @param dataInici
     * @param dataFin
     * @param estat
     * @return
     */
    @Query("SELECT r FROM Reserva r WHERE r.estat = :estat AND r.dataInici <= :dataFin AND r.dataFin >= :dataInici")
    List<Reserva> findOverlappingReservations(@Param("dataInici") LocalDateTime dataInici,
            @Param("dataFin") LocalDateTime dataFin,
            @Param("estat") EstatReserva estat);

    @Query("SELECT r FROM Reserva r WHERE "
            + "CAST(r.id AS string) LIKE %:query% OR "
            + "r.client.dni LIKE %:query% OR "
            + "r.client.email LIKE %:query% OR "
            + "r.client.nom LIKE %:query% OR "
            + "r.client.cognoms LIKE %:query% OR "
            + "r.vehicle.matricula LIKE %:query%")
    List<Reserva> findReservasByQuery(@Param("query") String query);
    
    
     @Query("SELECT r FROM Reserva r WHERE r.client.username = :username")
    List<Reserva> findByClientUsername(@Param("username") String username);
    
    @Query("SELECT r FROM Reserva r WHERE r.id = :id")
List<Reserva> findReservasByIdExacto(@Param("id") Long id);
}


