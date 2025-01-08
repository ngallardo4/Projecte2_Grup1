/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.entity.mysql;

import cat.copernic.ranare.enums.TipusCombustio;
import cat.copernic.ranare.enums.TipusTransmissio;
import cat.copernic.ranare.enums.TipusVehicle;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Classe que representa un vehicle dins del sistema de gestió.
 * Aquesta entitat conté la informació del vehicle, com la matrícula, tipus de combustió, límits de quilometratge,
 * preus de lloguer, fiances i les relacions amb altres entitats com les incidències i la localització.
 * @author ngall
 * @version 21/11/2024.1
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    
    /**
     * Matrícula del vehicle, que actua com a identificador únic dins del sistema.
     */
    @Id
    @NotBlank(message = "La matrícula és obligatòria.")
    @Pattern(regexp = "[A-Z0-9]{7}", message = "La matrícula ha de tenir 7 caràcters alfanumèrics.")
    private String matricula;
    
    @Column(name = "nom_vehicle")
    @NotBlank(message = "El nom del vehicle és obligatori.")
    @Size(min = 3, max = 50, message = "El nom del vehicle ha de tenir entre 3 i 50 caràcters.")
    private String nomVehicle;
    /**
     * Tipus de combustible que utilitza el vehicle.
     * Es tracta d'un tipus enum que pot tenir valors com 'Elèctric', 'Híbrid' o 'Combustió'.
     */
    @Enumerated(EnumType.STRING)
    @NotNull(message = "El tipus de combustió és obligatori.")
    private TipusCombustio combustio;
    
    /**
     * Tipus de transmissió que utilitza el vehicle.
     * Es tracta d'un tipus enum que pot tenir valors com 'Manual' o 'Automàtic'.
     */
    @Enumerated(EnumType.STRING)
    @NotNull(message = "El tipus de transmissió és obligatori.")
    private TipusTransmissio transmissio;
    
    /**
     * Límit de quilometratge permès per al vehicle.
     * Aquesta propietat especifica el nombre màxim de quilòmetres que es poden recórrer amb el vehicle abans d'arribar al límit.
     */
    @Column(name = "limit_quilometratge")
    @Positive(message = "El límit de quilometratge ha de ser un valor positiu i major que 0.")
    private int limitQuilometratge;

    /**
     * Preu per hora de lloguer del vehicle.
     * Aquesta propietat defineix el cost per cada hora de lloguer d'aquest vehicle.
     */
    @Column(name = "preu_per_hora_lloguer")
    @NotNull(message = "El preu per hora de lloguer és obligatori.")
    @Positive(message = "El preu per hora ha de ser un valor positiu.")
    private double preuPerHoraLloguer;

    /**
     * Nombre mínim d'hores per al lloguer del vehicle.
     * Aquesta propietat indica el nombre mínim d'hores que es pot llogar el vehicle.
     */
    @Column(name = "minim_hores_lloguer")
    @Min(value = 1, message = "El nombre mínim d'hores ha de ser almenys 1.")
    private int minimHoresLloguer;

    /**
     * Nombre màxim d'hores per al lloguer del vehicle.
     * Aquesta propietat especifica el nombre màxim d'hores per al lloguer del vehicle.
     */
    @Column(name = "maxim_hores_lloguer")
    @Min(value = 1, message = "El nombre màxim d'hores ha de ser almenys 1.")
    private int maximHoresLloguer;

    /**
     * Fiança estàndard que es requereix per al lloguer del vehicle.
     * Aquesta propietat especifica la quantitat de diners que s'ha de dipositar com a garantia en el moment del lloguer.
     */
    @Column(name = "fianca_standard")
    @NotNull(message = "La fiança estàndar és obligatoria.")
    @Positive(message = "La fiança ha de ser un valor positiu.")
    private double fiancaStandard;

    /**
     * Comentaris addicionals que l'agent pot afegir sobre el vehicle.
     * Aquesta propietat emmagatzema una cadena de text que permet afegir observacions específiques del vehicle.
     */
    @Column(name = "comentaris_agent", length = 2000)
    @Size(max = 2000, message = "Els comentaris no poden superar els 2000 caràcters.")
    private String comentarisAgent;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "El tipus de vehicle és obligatori.")
    private TipusVehicle tipus;
    
    @NotNull(message = "El nombre de passatgers és obligatori.")
    @Positive(message = "El nombre de passatgers ha de ser positiu.")
    private int passatgers;
    
    @NotNull(message = "És obligatori posar la disponibilitat del vehicle.")
    private boolean disponibilitat;
    
    @NotNull(message = "És obligatori posar la potència del vehicle.")
    @Positive(message = "La potència ha de ser un valor positiu.")
    private int potencia;
    
    @Column(name = "imatge_vehicle", columnDefinition = "LONGBLOB")
    private byte[] imatgeVehicle;
    
    @Column(name = "pdf_id")
    private String pdfId;
    
    /**
     * Relació entre Vehicle i Incidència.
     * Aquesta relació es defineix com un "OneToMany" ja que un vehicle pot tenir diverses incidències associades.
     */
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private List<Incidencia> incidencies;
    
    /**
     * Relació entre Vehicle i Localització.
     * Un vehicle està associat a una localització mitjançant el codi postal.
     * La relació es defineix com a "ManyToOne", ja que diversos vehicles poden compartir la mateixa localització.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "localitzacio", referencedColumnName = "codi_postal")
    private Localitzacio localitzacio;
    
    
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reserves = new ArrayList<>();
}
