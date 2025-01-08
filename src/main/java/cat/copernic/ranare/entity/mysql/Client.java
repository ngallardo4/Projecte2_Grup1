/*
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.entity.mysql;

import cat.copernic.ranare.enums.Reputacio;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.Binary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author Raú
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Client implements UserDetails {

    /**
     * Document Nacional d'Identitat (DNI). Actua com a clau primària única.
     */
    @Id
    @Column(nullable = false, unique = true, length = 9)
    @NotNull(message = "{dni.NotNull}") // Mensaje de error para campo DNI no nulo
    @Pattern(regexp = "^[0-9]{8}[A-Za-z]$", message = "{dni.Pattern}")
    @Size(min = 9, max = 9, message = "{dni.Size}") // Mensaje de error para tamaño de DNI

    private String dni;


    @Column(nullable = false)
    @NotNull(message = "{nom.NotNull}") // Mensaje de error para campo nombre no nulo
    @Size(min = 2, max = 100, message = "{nom.Size}") // Mensaje de error para tamaño del nombre
    private String nom;

    @Column(nullable = false)
    @NotNull(message = "cognoms.NotNull")
    @Size(min = 2, max = 200, message = "{cognoms.Size}")
    private String cognoms;
    
    
    /**
     * Nacionalitat del client. Obligatori.
     */
    @Column(nullable = false)
    @NotNull(message = "{nacionalitat.NotNull}")
    private String nacionalitat;

    /**
     * Telèfon de contacte del client. Obligatori i ha de tenir 9 dígits.
     */
    @Column(nullable = false, length = 9)
    @NotNull(message = "{telefon.NotNull}")
    @Pattern(regexp = "^[0-9]{9}$", message = "{telefon.Pattern}")
    private String telefon;
    
    
    /**
     * Nom d'usuari per a l'autenticació del client.
     */
    @Column(nullable = false, unique = true)
    @NotNull(message = "{username.NotNull}")
    private String username;

    /**
     * Contrasenya del client. No es visible en detalles ni se expone.
     */
    @Column(nullable = false)
    @NotNull(message = "{pwd.NotNull}")
    private String pwd;

    /**
     * Correu electrònic de l'usuari.
     */
    @Column(nullable = false, unique = true)
    @Email(message = "{email.Email}")
    @NotNull(message = "{email.NotNull}")
    private String email;

    /**
     * Adreça física del client.
     */
    @Column(nullable = false)
    @NotNull(message = "{adreca.NotNull}")
    private String adreca;

    /**
     * País de residència del client.
     */
    @Column(nullable = false)
    @NotNull(message = "{pais.NotNull}")
    private String pais;

    /**
     * Ciutat de residència del client.
     */
    @Column(nullable = false)
    @NotNull(message = "{ciutat.NotNull}")
    @Size(min = 2, max = 200, message = "{ciutat.Size}")
    private String ciutat;

    /**
     * Codi postal associat a l'adreça del client.
     */
    @Column(nullable = false, length = 10)
    @NotNull(message = "{codiPostal.NotNull}")
    @Size(min = 2, max = 10, message = "{codiPostal.Size}")
    private String codiPostal;

    @ElementCollection
    @CollectionTable(name = "client_documents", joinColumns = @JoinColumn(name = "dni"))
    @Column(name = "document_reference")
    private List<String> documents;

    /**
     * Número de la targeta de crèdit associada al client.
     */
    @Column(nullable = false)
    @NotNull(message = "{numeroTarjetaCredit.NotNull}")
    private String numeroTarjetaCredit;

    /**
     * Reputació del client. Pot ser "NORMAL" o "PREMIUM".
     */
    @Column(nullable = false)
    private Reputacio reputacio;
    
    @Column(nullable = false)
    private boolean actiu;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reserva> reserves;
    
    /**
     * Data de naixement del client.
     */
    @Past (message = "{dataNaixement.Past}")
    @Column(nullable = false)
    private LocalDate dataNaixement;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.pwd;
    }
    
    

    /**
     * Referència a la documentació en la base no relacional. Aquest camp pot
     * apuntar al document corresponent a MongoDB (pot ser un ID o una URL).
     
    @Column(nullable = true)
    private String referenciaDocumentacio;*/
    
    
    
}  

