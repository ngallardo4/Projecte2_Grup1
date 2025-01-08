/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.entity.mysql;

import lombok.Data;

/**
 *
 * @author Raú
 */
@Data
public class LocalitzacioDTO {
    private String codiPostal;
    private String ciutat;

    public LocalitzacioDTO(Localitzacio localitzacio) {
        this.codiPostal = localitzacio.getCodiPostal();
        this.ciutat = localitzacio.getCiutat();
    }
}
