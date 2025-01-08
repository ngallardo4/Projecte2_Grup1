/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.entity.mysql;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Representa una localització dins del sistema, amb la seva informació com el codi postal, 
 * adreça, tipus de localització, horari d'obertura i de tancament.
 * Una localització està relacionada amb un agent (relació un a un) i amb diversos vehicles 
 * (relació un a molts).
 * 
 * La classe està mapejada a una taula de la base de dades utilitzant JPA.
 * Utilitza les anotacions de Lombok per generar automàticament els mètodes getters, setters, constructors, 
 * i altres mètodes útils per la classe.
 * 
 * @author reyes
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "codiPostal")
public class Localitzacio {
    
    @Id
    @Column(name = "codi_postal")
    private String codiPostal;
    
    @Column(length= 255)
    @Size(min = 3, max = 255, message = "La adreça ha de tenir entre 3 i 255 caràcters")
    private String adreca;
    
    @Size(min = 2, max = 200, message = "La ciutat ha de tenir entre 2 i 200 caràcters")
    private String ciutat;
    
    @Size(min = 3, max = 200, message = "El pais ha de tenir entre 3 i 200 caràcters")
    private String pais;
    
    /**
     * Tipus de localització (pot ser una parking, un carrer, un aeroport, etc.).
     */
    @Size(min = 3, max = 200, message = "El tipus ha de tenir entre 3 i 200 caràcters")
    private String tipus;
    
    @Column(name = "horari_apertura")
    private LocalTime horariApertura;
    
    @Column(name = "horari_tancament")
    private LocalTime horariTancament;
    
    
    /**
     * Agent assignat a aquesta localització. Relació un a un amb la classe {@link Agent}.
     * El camp `dni_agent` es refereix a la clau primària de l'entitat {@link Agent} (el camp `dni`).
     */
    @OneToOne
    @JoinColumn(name = "dni_agent", referencedColumnName = "dni")
    private Agent agent;
    
    /**
     * Vehicles associats a aquesta localització. Relació un a molts amb la classe {@link Vehicle}.
     * Les operacions de persistència en una localització també es propagaran als vehicles associats.
     */
    @OneToMany(mappedBy = "localitzacio",cascade = CascadeType.ALL)
    
    private Set<Vehicle> vehicles;
    
    @Override
    public String toString(){
         return "Localitzacio{" +
               "codiPostal='" + codiPostal + '\'' +
               ", adreca='" + adreca + '\'' +
               ", ciutat='" + ciutat + '\'' +
               ", pais='" + pais + '\'' +
               ", tipus='" + tipus + '\'' +
               ", horariApertura=" + horariApertura +
               ", horariTancament=" + horariTancament + "}";
    }
}
