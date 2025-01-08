/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.entity.mysql;

import cat.copernic.ranare.enums.EstatReserva;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Raú
 */
/**
 * Classe que representa una reserva en la base de dades relacional.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Reserva {

    /**
     * Identificador únic de la reserva. Es genera automàticament a la base de
     * dades.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Data i hora d'inici de la reserva.
     */
    @Column(nullable = false)
    private LocalDateTime dataInici;

    /**
     * Data i hora de finalització de la reserva.
     */
    @Column(nullable = false)
    private LocalDateTime dataFin;

    /**
     * Fiança associada a la reserva. Aquest camp és obligatori.
     */
    @Column(nullable = false)
    private Double fianca;

    /**
     * Cost total de la reserva. Inclou el preu base i possibles descomptes.
     */
    @Column(nullable = false)
    private Double costReserva;

    /**
     * Estat actual de la reserva. Pot ser "ACTIVA", "ANULADA", "FINALITZADA".
     */
    @Enumerated(EnumType.STRING) // Garantir valors consistents
    @Column(nullable = false)
    private EstatReserva estat;

    /**
     * Client que realitza la reserva. Relació Molts a Un amb l'entitat Client.
     * Un client pot realitzar moltes reserves.
     */
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "client_dni", nullable = false)
    private Client client;

    /**
     * Vehicle associat a la reserva. Relació Molts a Un amb l'entitat Vehicle.
     * Un vehicle pot ser reservat diverses vegades.
     */
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "vehiculo_id", referencedColumnName = "matricula", nullable = false)
    private Vehicle vehicle;

    private boolean lliurament = false; // Inicializado como falso

    private boolean devolucio = false; // Inicializado como falso

}
