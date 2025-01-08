/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.ranare.repository.mysql;

import cat.copernic.ranare.entity.mysql.Agent;
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
 * @author reyes
 */
@Repository
public interface AgentRepository extends JpaRepository<Agent, String>{
    
    Optional<Agent> findByUsername(String username);
    
    Optional<Agent> findByEmail(String email);
    
    @Query("SELECT c FROM Agent c WHERE " +
           "Lower(c.dni) LIKE %:query%")
    List<Agent> buscarDni(@Param("query") String query);
    
    boolean existsByUsername(String username);
}
