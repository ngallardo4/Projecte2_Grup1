/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.entity.mongodb;

import cat.copernic.ranare.entity.mysql.Client;
import cat.copernic.ranare.entity.mysql.Localitzacio;
import cat.copernic.ranare.entity.mysql.Vehicle;
import cat.copernic.ranare.enums.EstatReserva;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Representa l'històric d'una reserva al sistema. Aquesta classe es mapeja a la
 * col·lecció "historic_reserves" de la base de dades no relacional. Conté
 * informació sobre la reserva, així com referències a altres entitats.
 *
 * @author Raú
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "historic_reserva") // Col·lecció a MongoDB
public class HistoricReserva {

    @Id
    private String id;
    private String idReserva; // ID de la reserva en la base de datos principal
    private String accio; // Ejemplo: "CREAR", "ACTUALITZAR", "ELIMINAR", etc.
    private String estat; // Representación en texto del estado de la reserva
    private LocalDateTime dataInici; // Cambiar a LocalDateTime
    private LocalDateTime dataFin; // Cambiar a LocalDateTime
    private Double costReserva;
    private Double fianca;
    private LocalDateTime timestamp; // Fecha y hora del cambio, almacenada como LocalDateTime

    // Nuevo campo para almacenar solo el DNI del cliente
    private String dniCliente;

    // Nuevo campo para almacenar solo la matrícula del vehículo
    private String matriculaVehiculo;

    // Getters y Setters
}
