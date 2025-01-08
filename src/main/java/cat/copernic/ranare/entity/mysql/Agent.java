/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.entity.mysql;

import cat.copernic.ranare.enums.Rol;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Representa un agent dins del sistema, amb la seva informació personal i el rol assignat.
 * Un agent està relacionat amb una localització, i pot ser assignat a una sola localització a través de la relació un a un.
 * 
 * La classe està mapejada a una taula de la base de dades utilitzant JPA.
 * Utilitza les anotacions de Lombok per generar automàticament els mètodes getters, setters, constructors, 
 * i altres mètodes útils per la classe.
 * 
 * 
 * La relació amb la classe {@link Localitzacio} és de tipus un a un, i l'entitat {@link Localitzacio} 
 * té el camp `agent` que fa referència a aquesta classe.
 * 
 * @author reyes
 */

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Agent extends Client{
    
    /**
     * Rol de l'agent, determinat per un valor de l'enumeració {@link Rol}.
     */
    @Enumerated(EnumType.STRING)
    private Rol rol;
    
    
    /**
     * Localització assignada a l'agent. Relació un a un amb la classe {@link Localitzacio}.
     * La propietat `mappedBy` indica que aquesta relació és gestionada per la classe {@link Localitzacio}.
     * 
     * {@link CascadeType.ALL} indica que les operacions de persistència en un agent també 
     * es propagaran a la localització associada.
     */
    @OneToOne(mappedBy = "agent",cascade = CascadeType.MERGE)
    private Localitzacio localitzacio;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }
    
    
}
