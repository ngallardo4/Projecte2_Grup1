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
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Classe que representa els documents associats a un vehicle a la base de dades MongoDB.
 * Aquesta entitat permet emmagatzemar i gestionar els documents (com PDF o imatges) relacionats amb un vehicle.
 * Aquesta classe inclou constructors per defecte i amb tots els arguments, i genera automàticament
 * els mètodes equals, hashCode, i toString mitjançant Lombok.
 * @author ngall
 * @version 21/11/2024.1
 */
@Document(collection = "documents_vehicle")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentsVehicle {
    
    /**
     * Identificador únic de l'entrada de documents del vehicle.
     */
    @Id
    private String id;
    
    /**
     * Llista de documents associats al vehicle, com PDF o imatges codificades en Base64.
     */
    @Field("documents")
    private List<Binary> documents;

}
