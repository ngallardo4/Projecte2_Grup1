/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.service.mysql;

import cat.copernic.ranare.entity.mysql.Agent;
import cat.copernic.ranare.entity.mysql.Localitzacio;
import cat.copernic.ranare.entity.mysql.Vehicle;
import cat.copernic.ranare.exceptions.DuplicateResourceException;
import cat.copernic.ranare.exceptions.EntitatRelacionadaException;
import cat.copernic.ranare.exceptions.InvalidCodiPostalException;
import cat.copernic.ranare.exceptions.InvalidHorariException;
import cat.copernic.ranare.repository.mysql.AgentRepository;
import cat.copernic.ranare.repository.mysql.LocalitzacioRepository;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servei per gestionar les operacions relacionades amb les localitzacions.
 * Proporciona funcionalitats per obtenir totes les localitzacions i guardar una nova localització.
 * @author reyes
 */

@Service
public class LocalitzacioService {
    
    /**
     * Repositori de localitzacions utilitzat per interactuar amb la base de dades.
     */
    @Autowired
    LocalitzacioRepository localitzacioRepository;
    
    @Autowired
    AgentRepository agentRepository;
    
    /**
     * Obté totes les localitzacions emmagatzemades a la base de dades.
     *
     * @return Una llista amb totes les localitzacions.
     */
    public List<Localitzacio> getallLocalitzacio(){
        return localitzacioRepository.findAll();
    }
    
    /**
     * Guarda una nova localització a la base de dades.
     * Verifica que el codi postal sigui vàlid abans de guardar.
     *
     * @param local La localització que es vol guardar.
     * @return La localització que s'ha guardat.
     * @throws InvalidCodiPostalException Si el codi postal no conté només números.
     */
    public Localitzacio saveLocalitzacio(Localitzacio local){
        
        if(!validarCodiPostal(local.getCodiPostal()))
            throw new InvalidCodiPostalException("El codi postal ha de contenir només números.");
        
        Optional<Localitzacio> existeixLocal = localitzacioRepository.findById(local.getCodiPostal());
        if (existeixLocal.isPresent()) {
            // Si ja existeix llança una excepció
            throw new DuplicateResourceException("El codi postal ja està assignat a una altre localització.");
        }
        
        if(local.getAgent()!= null){
            Optional<Localitzacio> existeixLocalAgent = localitzacioRepository.findByAgentDni(local.getAgent().getDni());
            if(existeixLocalAgent.isPresent()){
                throw new DuplicateResourceException("Aquest agent ja està assignat a una altra localització.");
            }
        }
            
        return localitzacioRepository.save(local);
    }
    
    /**
     * Valida si el codi postal proporcionat és vàlid.
     * El codi postal es considera vàlid si no és null i només conté números.
     *
     * @param cp El codi postal a validar.
     * @return True si el codi postal és vàlid, false en cas contrari.
     */
    private boolean validarCodiPostal(String cp){
        return cp!= null && cp.matches("\\d+");
    }
    
    //comprovar si l'horari d'apertura es abans de l'horari de tancament
    public void validarHorari(LocalTime horariApertura, LocalTime horariTancament) throws InvalidHorariException{
        if(horariApertura.isAfter(horariTancament) || horariApertura.equals(horariTancament))
            throw new InvalidHorariException("L'horari de tancament ha de ser més tard que el d'apertura");
    }
    
    //retorna els vehicles associats a una localitzacio, es comproba també que el codi postal existeixi
    public Set<Vehicle> getVehiclePerLocalitzacio(String codiPostal){
        Localitzacio localitzacio = localitzacioRepository.trobarVehiclesPerCodiPostal(codiPostal)
                .orElseThrow(() -> new InvalidCodiPostalException("Localització amb el codi postal " + codiPostal +" no trobada"));
        
        return localitzacio.getVehicles();
    }
    
    /**
     * Retorna una localització pel seu codi postal.
     * 
     * @param codiPostal El codi postal de la localització.
     * @return La localització corresponent al codi postal.
     * @throws InvalidCodiPostalException Si no es troba cap localització amb el codi postal.
     */
    public Localitzacio getLocalitzacioPerCodiPostal(String codiPostal){
        return localitzacioRepository.findById(codiPostal)
                .orElseThrow(() -> new InvalidCodiPostalException("Localització amb el codi postal " + codiPostal +" no trobada"));
    }
    
    public void updateLocalitzacio(Localitzacio localitzacio){
        Optional<Localitzacio> localitzacioExisteix = localitzacioRepository.findById(localitzacio.getCodiPostal());
        if(localitzacioExisteix.isPresent()){
            //recuperem dades d'aquesta localitzacio
            Localitzacio localitzacioExistent = localitzacioExisteix.get();
            
            //actualitzem les dades
            localitzacioExistent.setAdreca(localitzacio.getAdreca());
            localitzacioExistent.setAgent(localitzacio.getAgent());
            localitzacioExistent.setCiutat(localitzacio.getCiutat());
            localitzacioExistent.setHorariApertura(localitzacio.getHorariApertura());
            localitzacioExistent.setHorariTancament(localitzacio.getHorariTancament());
            localitzacioExistent.setPais(localitzacio.getPais());
            localitzacioExistent.setTipus(localitzacio.getTipus());
            
            localitzacioRepository.save(localitzacioExistent);
        }else{
            throw new InvalidCodiPostalException("Localització amb el codi postal " + localitzacio.getCodiPostal() +" no trobada");
        }
    }
    
    public String horariAperturaFormategat (Localitzacio localitzacio){
        //Possar les hores i minuts en format correcte per al HTML
        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");
        String formatHora = localitzacio.getHorariApertura().format(format);
        
        return formatHora;
    }
    
    public String horariTancamentFormategat (Localitzacio localitzacio){
        //Possar les hores i minuts en format correcte per al HTML
        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");
        String formatHora = localitzacio.getHorariTancament().format(format);
        
        return formatHora;
    }
    
    public void eliminarLocalitzacio(String codiPostal){
        Optional<Localitzacio> localitzacioExisteix = localitzacioRepository.findById(codiPostal);
        if(localitzacioExisteix.isPresent()){
            Localitzacio localitzacio = localitzacioExisteix.get();
            
            boolean vehiclesAsociats = !localitzacio.getVehicles().isEmpty();
            Agent agent = localitzacio.getAgent();
            
            if(vehiclesAsociats){
                String missatge = "No es pot eliminar la localització amb codi postal " + codiPostal + " perquè";
                if(vehiclesAsociats) missatge += " té vehicles asociats";
                throw new EntitatRelacionadaException(missatge);
            }
            
            if(agent != null){
                agent.setLocalitzacio(null);
                agentRepository.save(agent);
            }
            
            localitzacioRepository.delete(localitzacio);
        }else{
            throw new InvalidCodiPostalException("Localització amb el codi postal " + codiPostal +" no trobada");
        }
    }
}
