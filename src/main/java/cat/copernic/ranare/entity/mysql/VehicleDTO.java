/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.entity.mysql;

import cat.copernic.ranare.enums.TipusCombustio;
import cat.copernic.ranare.enums.TipusVehicle;
import lombok.Data;

/**
 *
 * @author Raú
 */
@Data
public class VehicleDTO {

    private String matricula;
    private String nomVehicle;
    private TipusCombustio combustio;
    private double preuPerHoraLloguer;
    private double fiancaStandard;
    private int minimHoresLloguer;
    private int maximHoresLloguer;
    private TipusVehicle tipus;
    private int passatgers;
    private boolean disponibilitat;
    private LocalitzacioDTO localitzacio;
    private String ciutat;

    public VehicleDTO(Vehicle vehicle) {
        this.matricula = vehicle.getMatricula();
        this.nomVehicle = vehicle.getNomVehicle();
        this.combustio = vehicle.getCombustio();
        this.preuPerHoraLloguer = vehicle.getPreuPerHoraLloguer();
        this.fiancaStandard = vehicle.getFiancaStandard();
        this.minimHoresLloguer = vehicle.getMinimHoresLloguer();
        this.maximHoresLloguer = vehicle.getMaximHoresLloguer();
        this.tipus = vehicle.getTipus();
        this.passatgers = vehicle.getPassatgers();
        this.disponibilitat = vehicle.isDisponibilitat();
        if (vehicle.getLocalitzacio() != null) {
            this.localitzacio = new LocalitzacioDTO(vehicle.getLocalitzacio());
            this.ciutat = vehicle.getLocalitzacio().getCiutat();
            
        }
    }
}
