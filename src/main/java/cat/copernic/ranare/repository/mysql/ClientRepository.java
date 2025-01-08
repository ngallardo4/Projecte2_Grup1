/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.repository.mysql;

import cat.copernic.ranare.entity.mysql.Client;
import cat.copernic.ranare.enums.Rol;
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
public interface ClientRepository extends JpaRepository<Client, String> {

    @Override
    Optional<Client> findById(String dni); // Para buscar por DNI

    Optional<Client> findByEmail(String email); // Para buscar por email

    Optional<Client> findByUsername(String username);

    List<Client> findByActiu(boolean actiu);

    /**
     *
     * @return
     */
    @Query("SELECT c FROM Client c WHERE c.dni NOT IN (SELECT a.dni FROM Agent a)")
    List<Client> findAllClientsExcludingAgents();

    @Query("SELECT c FROM Client c WHERE c.dni NOT IN (SELECT a.dni FROM Agent a) AND ("
            + "LOWER(c.dni) LIKE %:query% OR "
            + "LOWER(c.email) LIKE %:query% OR "
            + "LOWER(c.telefon) LIKE %:query% OR "
            + "LOWER(c.cognoms) LIKE %:query% OR "
            + "LOWER(c.nacionalitat) LIKE %:query%)")
    List<Client> searchByFilters(@Param("query") String query);

    boolean existsByUsername(String username);

}
