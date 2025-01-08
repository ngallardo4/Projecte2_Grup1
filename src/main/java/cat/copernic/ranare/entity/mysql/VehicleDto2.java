/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.entity.mysql;

import cat.copernic.ranare.enums.TipusCombustio;
import cat.copernic.ranare.enums.TipusTransmissio;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author reyes
 */
@Data
@AllArgsConstructor
public class VehicleDto2 {
    private String matricula;
    private String nomVehicle;
    private String imgBase64;
    private String codiPostal;
    private TipusCombustio combustio;
    private TipusTransmissio transmissio;
    private int limitQuilometratge;
    private double fianca;
    private Double preuPerHoraLloguer;
}
