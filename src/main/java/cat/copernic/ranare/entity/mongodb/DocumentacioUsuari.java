/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.entity.mongodb;

import cat.copernic.ranare.enums.DocumentState;
import cat.copernic.ranare.enums.DocumentType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Representa la informació addicional i no estructurada d'un usuari. Aquesta
 * informació és comuna per a tots els tipus d'usuaris (ADMIN, CLIENT, etc.). Es
 * mapeja a la col·lecció "documentacio_usuaris" de la base de dades no
 * relacional.
 *
 * @author Raú
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "documentacio_usuaris")
public class DocumentacioUsuari {

    @Id
    private String id;

    /**
     * ID del usuario relacionado con esta documentación.
     */
    private String userId;

    /**
     * Tipo de documento (Ej.: DNI, Licencia de conducir).
     */
    private DocumentType documentType;

    /**
     * Nombre del documento (Ej.: DNI, Carnet de Conducir).
     */
    private String documentName;

    /**
     * Archivo binario del anverso.
     */
    private byte[] frontFile;

    /**
     * Archivo binario del reverso.
     */
    private byte[] backFile;

    /**
     * Fecha de creación del documento.
     */
    private LocalDateTime creationDate;

    /**
     * Estado del documento (Ej.: ACTIVA, CADUCADA).
     */
    private DocumentState documentState;

    // Getters y Setters
    private String frontFileMimeType; // Tipo MIME del archivo (ej: "image/jpeg", "application/pdf")
    private String backFileMimeType;
}
