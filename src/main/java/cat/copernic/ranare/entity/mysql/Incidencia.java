/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.entity.mysql;

import cat.copernic.ranare.enums.EstatIncidencia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Classe que representa una incidència que afecta un vehicle a la base de dades MySQL.
 * Aquesta entitat conté informació rellevant sobre una incidència, com ara el seu estat,
 * motiu, cost i les dates d'inici i finalització.
 * Aquesta classe inclou constructors per defecte i amb tots els arguments, i genera automàticament
 * els mètodes equals, hashCode, i toString mitjançant Lombok.
 * @author ngall
 * @version 21/11/2024.1
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Incidencia {
    
    /**
     * Identificador únic autoincremental de la incidència.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_incidencia")
    private Long idIncidencia;
    
    /**
     * Estat actual de la incidència, que pot ser obert o tancat.
     */
    @Enumerated(EnumType.STRING)
    private EstatIncidencia estat;
    
    /**
     * Motiu o causa de la incidència (accident, revisió, desperfecte, etc.).
     */
    private String motiu;
    
    /**
     * Cost associat a la incidència.
     */
    private double cost;
    
    /**
     * Data d'inici de la incidència.
     */
    @Column(name = "data_inici")
    private LocalDateTime dataInici; 
    
    /**
     * Data de finalització de la incidència (si aplica).
     */
    @Column(name = "data_final")
    private LocalDateTime dataFinal;
    
    /**
     * Culpabilitat de l'incidència, False = Culpa del vehicle, True = Culpa de Usuari.
     */
    private boolean culpabilitat;
    
    /**
     * Identificador dels documents relacionats amb la incidència.
     * Aquest camp és Transient, això evita que sigui persistit a la base de dades SQL
     */
    private List<String> documentsIncidenciaId;
    
    private List<String> imatgesIncidenciesIDs;
    
    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    
}
