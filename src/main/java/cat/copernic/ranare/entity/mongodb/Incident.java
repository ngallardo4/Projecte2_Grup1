/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.entity.mongodb;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Representa un incident registrat al sistema.
 * Aquesta classe es mapeja a la col·lecció "Incidents" de la base de dades no relacional.
 * Conté informació sobre l'estat, el motiu, el cost, les dates d'inici i finalització,
 * així com els documents relacionats amb l'incident.
 * 
 * @author Raú
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "incidents") // Col·lecció a MongoDB
public class Incident {

    /**
     * Estat actual de l'incident (exemple: "OBERT", "TANCAT").
     */
    @Field("estat")
    private String estat;

    /**
     * Motiu o descripció de l'incident.
     */
    @Field("motiu")
    private String motiu;

    /**
     * Cost associat a l'incident.
     */
    @Field("cost")
    private double cost;

    /**
     * Data d'inici de l'incident.
     */
    @Field("data_inici")
    private Date dataInici;

    /**
     * Data de finalització de l'incident.
     */
    @Field("data_final")
    private Date dataFinal;

    /**
     * Llista de documents relacionats amb l'incident (URLs o identificadors).
     */
    @Field("documents")
    private List<String> documents;
}