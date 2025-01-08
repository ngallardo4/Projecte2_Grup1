
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.service.mysql;

import cat.copernic.ranare.entity.mongodb.DocumentacioUsuari2;
import cat.copernic.ranare.entity.mysql.Agent;
import cat.copernic.ranare.repository.mysql.ClientRepository;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import java.util.List;
import cat.copernic.ranare.exceptions.ClientNotFoundException;
import cat.copernic.ranare.entity.mysql.Client;
import cat.copernic.ranare.enums.Reputacio;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Raú
 */
// Crear o actualizar un cliente
@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;
    
   @Autowired
   private PasswordEncoder passwordEncoder;

    /**
     * Desa un client a la base de dades.
     *
     * Detecta si és una creació o una modificació en funció de l'existència del
     * client.
     *
     * @param client El client a desar.
     * @param isUpdate
     * @param bindingResult
     * @return `true` si l'operació és correcta, `false` si falla per
     * duplicació.
     */
    public Client saveClient(Client client, boolean isUpdate, BindingResult bindingResult, boolean isAdmin) {
        // Verificación de duplicados sin interrumpir el flujo
        List<String> errorMessages = new ArrayList<>();

        // Asegurarnos de que el DNI esté en mayúsculas
        if (client.getDni() != null) {
            client.setDni(client.getDni().toUpperCase());
        }

        // **Validación del DNI** (comprobamos formato y letra)
        if (client.getDni() != null && !esDniValido(client.getDni())) {
            bindingResult.rejectValue("dni", "invalid.dni", "El DNI no és vàlid.");
        }

        // Verificación de duplicado para DNI
        if (isUpdate) {
            Optional<Client> existingClientByDni = clientRepository.findById(client.getDni());
            if (existingClientByDni.isPresent() && !existingClientByDni.get().getDni().equals(client.getDni())) {
                bindingResult.rejectValue("dni", "duplicate.dni", "El DNI ja està registrat.");
            }
        } else {
            Optional<Client> existingClientByDni = clientRepository.findById(client.getDni());
            if (existingClientByDni.isPresent()) {
                bindingResult.rejectValue("dni", "duplicate.dni", "El DNI ja està registrat.");
            }
        }

        // Verificación de duplicado para email
        Optional<Client> existingClientByEmail = clientRepository.findByEmail(client.getEmail());
        if (existingClientByEmail.isPresent()) {
            bindingResult.rejectValue("email", "duplicate.email", "El correu electrònic ja està registrat.");
        }

        // **Verificación de duplicado para username**
        Optional<Client> existingClientByUsername = clientRepository.findByUsername(client.getUsername());
        if (existingClientByUsername.isPresent()) {
            bindingResult.rejectValue("username", "duplicate.username", "El nom d'usuari ja està registrat.");
        }

        // Si se encontraron errores, no procedemos a guardar el cliente
        if (bindingResult.hasErrors()) {
            return null; // Si hay errores de validación, no guardamos el cliente
        }
        
        //encriptem la password
        if(client.getPwd() != null && !client.getPwd().isEmpty()){
            client.setPwd(passwordEncoder.encode(client.getPwd()));
        }
        
        //si es crea un usuari sense que siguie l'admin, l'usuari apareix amb reputació normal i client desactivat, sino es activat
        if(!isAdmin){
        client.setActiu(false);
        client.setReputacio(Reputacio.NORMAL);   
        }else{
            client.setActiu(true);
        }
             
        // Si no hay duplicados, guardamos el cliente
        return clientRepository.save(client);
    }

    /**
     * Obté un client pel seu DNI.
     *
     * @param dni El DNI del client.
     * @return Una instància `Optional` del client.
     */
    public Optional<Client> getClientById(String dni) {
        return clientRepository.findById(dni);
    }

    /**
     * Obté tots els clients de la base de dades.
     *
     * @return Una llista de clients.
     */
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    /**
     * Elimina un client pel seu DNI.
     *
     * @param dni El DNI del client a eliminar.s
     */
    public void deleteClient(String dni) {
        Optional<Client> clientOpt = clientRepository.findById(dni);
        if (clientOpt.isPresent()) {
            clientRepository.delete(clientOpt.get());
        } else {
            throw new ClientNotFoundException("L'agent amb DNI " + dni + " no existeix");
        }

    }

    /**
     * Retorna una llista de clients que exclou els agents.
     *
     * @return Una llista de clients (sense incloure els agents).
     */
    public List<Client> getOnlyClients() {
        return clientRepository.findAllClientsExcludingAgents();
    }

    /**
     * Cerca clients en funció d'un filtre proporcionat. El filtre pot ser
     * parcial i buscarà coincidències en els camps rellevants (com ara nom,
     * cognoms, correu electrònic, etc.).
     *
     * @param query El filtre de cerca, es convertirà a minúscules per realitzar
     * una cerca insensible a majúscules/minúscules.
     * @return Una llista de clients que coincideixen amb el filtre especificat.
     */
    public List<Client> searchClients(String query) {
        return clientRepository.searchByFilters(query.toLowerCase());

    }

    // Método para validar el DNI
    public boolean esDniValido(String dni) {
        // Verificar si el DNI sigue el formato correcto
        String regex = "^[0-9]{8}[A-Za-z]$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(dni);

        // Si el formato no es correcto, devolver falso
        if (!matcher.matches()) {
            return false;
        }

        // Si el formato es correcto, comprobar si la letra es válida
        char letraCalculada = calcularLetraDni(dni.substring(0, 8)); // Obtenemos la letra
        char letraDni = dni.charAt(8); // Letra que aparece en el DNI

        return letraCalculada == letraDni; // Comprobamos si la letra calculada es igual a la del DNI
    }

    // Método para calcular la letra del DNI
    private char calcularLetraDni(String numeroDni) {
        // El número del DNI como entero
        int numero = Integer.parseInt(numeroDni);

        // El array con las letras posibles para el DNI
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";

        // El cálculo de la letra según el algoritmo oficial
        int resto = numero % 23;
        return letras.charAt(resto);
    }
    
    public boolean existeixUsername(String username) {
        return clientRepository.findByUsername(username).isPresent();
    }
    public boolean existeixEmail(String email) {
        return clientRepository.findByEmail(email).isPresent();
    }
    
    public List<Client> getInactiveClients(){
        return clientRepository.findByActiu(false);
    }
    
    public void activarClient(String dni){
        Optional<Client> clientExisteix = clientRepository.findById(dni);
        if(clientExisteix.isPresent()){
            Client client = clientExisteix.get();
            client.setActiu(true);
            clientRepository.save(client);
        }
    }
    
    public Optional<Client> findByUsername(String username) {
        return clientRepository.findByUsername(username);
    }
    
    public Client usuariLogejat(){
        //obtenir l'usuari desde seguretat
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        //trobar l'usuari a la BBDD
        return clientRepository.findByUsername(username).orElseThrow(() -> new ClientNotFoundException("Usuari no trobat: " + username));
    }

}
