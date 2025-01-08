/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.service.mysql;

import cat.copernic.ranare.entity.mysql.Agent;
import cat.copernic.ranare.entity.mysql.Client;
import cat.copernic.ranare.enums.Rol;
import cat.copernic.ranare.exceptions.AgentNotFoundException;
import cat.copernic.ranare.exceptions.DuplicateResourceException;
import cat.copernic.ranare.exceptions.EntitatRelacionadaException;
import cat.copernic.ranare.repository.mysql.AgentRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author reyes
 */
@Service
public class AgentService {
    
    @Autowired
    private AgentRepository agentRepository;
    
    @Autowired
    private ClientService clientService;
    
    private static final Logger logger = LoggerFactory.getLogger(AgentService.class);
    
    public List<Agent> getAllAgents() {
        return agentRepository.findAll(); // Retorna una llista d'agents
    }
    
    //Aquesta anotació es per si alguna operació amb la BBDD falla, fa un rollback
    @Transactional
    public Agent crearAgent(Client client, Rol rol) {
        
        if(client.getDni() != null && !clientService.esDniValido(client.getDni())){
            throw new DuplicateResourceException("DNI no vàlid");
        }
        Optional<Client> existeixClient = clientService.getClientById(client.getDni());
        if(existeixClient.isPresent()){
            throw new DuplicateResourceException("El DNI ja està assignat a un client.");
        }
        
        // Verificar si ja existeix un agent amb el mateix DNI
        Optional<Agent> existingAgent = agentRepository.findById(client.getDni());
        if (existingAgent.isPresent()) {
            // Si ja existeix llança una excepció
            throw new DuplicateResourceException("El DNI ja està assignat a un altre agent.");
        }

        // Buidar la sessió per evitar la duplicació del objecte en la sessió d'Hibernate 

        Agent agent = Agent.builder()
                .dni(client.getDni())
                .nom(client.getNom())
                .cognoms(client.getCognoms())
                .username(client.getUsername())
                .pwd(client.getPwd())
                .nacionalitat(client.getNacionalitat())
                .dataNaixement(client.getDataNaixement())
                .email(client.getEmail())
                .numeroTarjetaCredit(client.getNumeroTarjetaCredit())
                .adreca(client.getAdreca())
                .pais(client.getPais())
                .ciutat(client.getCiutat())
                .codiPostal(client.getCodiPostal())
                .reputacio(client.getReputacio())
                .telefon(client.getTelefon())
                .rol(rol)
                .build();

        // Guardar el nou agent
        return agentRepository.save(agent);
    }
    
    public void guardarAgent(Agent agent){
        agentRepository.save(agent);
    }
    public List<Agent> filtrarAgentByDni(String dni) {
        return agentRepository.buscarDni(dni); // Busca un agent per el seu DNI
    }
    
    public Optional<Agent> findAgentByDni(String dni) {
        return agentRepository.findById(dni); // Busca un agent per el seu DNI
    }
    
    public void eliminarAgent(String dni) {
        //busquem agent
        Agent agent = agentRepository.findById(dni).orElseThrow(() -> 
        new AgentNotFoundException("L'agent amb DNI " + dni + " no existeix"));
        
        //verifquem si té una localització
        if(agent.getLocalitzacio() != null){
            throw new EntitatRelacionadaException("L'agent té una localització assignada. Primer has d'eliminar la localització.");
        }
        
        agentRepository.delete(agent);
    }
    
    public void modificarAgent(Agent agent){
        Optional<Agent> agentExisteix = agentRepository.findById(agent.getDni());
        if(agentExisteix.isPresent()){
            Agent agentExistent = agentExisteix.get();
            
            logger.info("Datos actuales del agente antes de modificar: {}", agentExistent);
            
            // Si es proporciona una nova contrasenya, l'actualitzem
        if (agent.getPwd() != null && !agent.getPwd().isEmpty()) {
            logger.info("Actualizando la contraseña del agente.");
            agentExistent.setPwd(agent.getPwd());
        }
            //actualitzar els camps
            agentExistent.setNom(agent.getNom());
            agentExistent.setCognoms(agent.getCognoms());
            agentExistent.setAdreca(agent.getAdreca());
            agentExistent.setCiutat(agent.getCiutat());
            agentExistent.setCodiPostal(agent.getCodiPostal());
            agentExistent.setDataNaixement(agent.getDataNaixement());
            agentExistent.setDni(agent.getDni());
            agentExistent.setEmail(agent.getEmail());
            agentExistent.setLocalitzacio(agent.getLocalitzacio());
            agentExistent.setNacionalitat(agent.getNacionalitat());
            agentExistent.setNumeroTarjetaCredit(agent.getNumeroTarjetaCredit());
            agentExistent.setPais(agent.getPais());
            agentExistent.setTelefon(agent.getTelefon());
            agentExistent.setReputacio(agent.getReputacio());
            agentExistent.setRol(agent.getRol());
            agentExistent.setUsername(agent.getUsername());
            
            logger.info("Datos del agente después de modificar: {}", agentExistent);
            
            agentRepository.save(agentExistent);
        }else{
            throw new AgentNotFoundException("Agent amb DNI " + agent.getDni() + " no trobat");
        }
    }
    
    public Agent getAgentPerDni(String dni){
        return agentRepository.findById(dni)
                .orElseThrow(() -> new AgentNotFoundException("Agent amb DNI  " + dni +" no trobat"));
    }
    
    public boolean existeixUsername(String username){
        return agentRepository.findByUsername(username).isPresent();
    }
    
    public boolean existeixEmail(String email){
        return agentRepository.findByEmail(email).isPresent();
    }
    
    public boolean isAdmin(Agent agent){

        return Rol.ADMIN.equals(agent.getRol());
    }
    
    public Optional<Agent> findByUsername(String username) {
        return agentRepository.findByUsername(username);
    }
}
