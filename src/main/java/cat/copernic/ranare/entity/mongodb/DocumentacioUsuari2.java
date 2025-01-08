/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.entity.mongodb;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Representa la informació addicional i no estructurada d'un usuari.
 * Aquesta informació és comuna per a tots els tipus d'usuaris (ADMIN, CLIENT, etc.).
 * Es mapeja a la col·lecció "documentacio_usuaris" de la base de dades no relacional.
 * 
 * @author Raú
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "documentacio_usuaris") // Col·lecció a MongoDB
public class DocumentacioUsuari2 {

    /**
     * Identificador únic del document a la col·lecció.
     * Generat automàticament per MongoDB si no es proporciona.
     */
    @Id
    private String id;
    private String nom;
    private byte[] contingut;
    private String tipusContingut;
    private LocalDateTime dataPujada;
}