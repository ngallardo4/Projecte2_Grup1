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
import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Classe que representa els documents associats a una incidència en la base de dades MongoDB.
 * Aquesta entitat emmagatzema informació relacionada amb els documents d'una incidència,
 * com poden ser imatges o PDF’s associats a cadascuna.
 * La col·lecció en MongoDB es denomina "documents_incidencia".
 * Aquesta classe inclou constructors per defecte i amb tots els arguments, i genera automàticament
 * els mètodes equals, hashCode, i toString mitjançant Lombok.
 * @author ngall
 * @version 21/11/2024.1
 */
@Document(collection = "documents_incidencia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentsIncidencia {
    
    /**
     * Identificador únic de la col·lecció de documents associats a una incidència.
     */
    @Id
    private String id;
    
    /**
     * Llista de documents associats a la incidència.
     * Aquesta llista pot contenir identificadors de documents com imatges, PDF's o altres arxius.
     */
    private List<Binary> documents; //Aquí guardarem tant els PDF's com les imatges
    
}
