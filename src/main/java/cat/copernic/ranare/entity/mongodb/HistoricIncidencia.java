/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.entity.mongodb;

import jakarta.persistence.Id;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Classe que representa l'historial d'incidències d'un vehicle a la base de dades MongoDB.
 * Aquesta entitat emmagatzema les incidències històriques associades a un vehicle específic, 
 * permetent mantenir un registre de les incidències passades.
 * Aquesta classe inclou constructors per defecte i amb tots els arguments, 
 * i utilitza Lombok per generar automàticament els mètodes equals, hashCode, i toString.
 * @author ngall
 * @version 21/11/2024.1
 */
@Document(collection = "historical_incidents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricIncidencia {
    
    /**
     * Identificador únic de l'historial d'incidències.
     */
    @Id
    private String id;
    
    /**
     * Identificador del vehicle associat a aquest historial d'incidències.
     */
    @Field("vehicle_id")
    private String vehicleId;
    
    /**
     * Llista d'incidències relacionades amb el vehicle, emmagatzemada en una classe anomenada Incident.
     */
    @Field("incidents")
    private List<Incident> incidents;
    
}
