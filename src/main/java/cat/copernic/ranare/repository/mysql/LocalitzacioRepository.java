/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.repository.mysql;

import cat.copernic.ranare.entity.mysql.Localitzacio;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Una interface que hereda de JpaRepository amb els seus propis mètodes per poder implementar-los en la 
 * capa service. També es poden definir mètodes personalitzats sense implementar-los
 * @author reyes
 */
@Repository
public interface LocalitzacioRepository extends JpaRepository <Localitzacio,String>{
    
    /*Utilitzem el Join Fetch ja que necessitem carregar les dades de vehicles, per defecte el oneToMany està configurat amb
    Lazy no en Eager, per tant no carregará les dades de la taula relacionada*/
    @Query("SELECT l FROM Localitzacio l LEFT JOIN FETCH l.vehicles WHERE l.codiPostal = :codiPostal")
    Optional<Localitzacio> trobarVehiclesPerCodiPostal(@Param("codiPostal") String codiPostal);
    
    Optional<Localitzacio> findByAgentDni(String agentDni);
    
}
