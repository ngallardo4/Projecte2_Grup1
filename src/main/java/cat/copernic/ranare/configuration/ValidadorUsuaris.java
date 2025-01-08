/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.configuration;

import cat.copernic.ranare.entity.mysql.Agent;
import cat.copernic.ranare.entity.mysql.Client;
import cat.copernic.ranare.exceptions.ClientNotFoundException;
import cat.copernic.ranare.exceptions.UsuariNoActivatException;
import cat.copernic.ranare.repository.mysql.AgentRepository;
import cat.copernic.ranare.repository.mysql.ClientRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author reyes
 */
@Service
public class ValidadorUsuaris implements UserDetailsService {
    
    public ValidadorUsuaris(){
        
    }
    
    @Autowired
    ClientRepository clientRepository;
    
    @Autowired
    AgentRepository agentRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Agent> agentExisteix = agentRepository.findByUsername(username);
        
        if(agentExisteix.isPresent()){
            Agent agent = agentExisteix.get();
            if(!agent.isActiu()){
                throw new UsuariNoActivatException("El compte està desactivat");
            }
           return agent;         
        }
        
        Optional<Client> clientExisteix = clientRepository.findByUsername(username);
        
        if(clientExisteix.isPresent()){
            Client client = clientExisteix.get();
            if(!client.isActiu()){
                throw new UsuariNoActivatException("El compte està desactivat");
            }
            return client;
        }
        
        throw new ClientNotFoundException("Usuari no trobat");
    }
    
}
