/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.repository.mongodb;

import cat.copernic.ranare.entity.mongodb.HistoricReserva;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Raú
 */
@Repository
public interface HistoricReservaRepository extends MongoRepository<HistoricReserva, String> {
    List<HistoricReserva> findByIdReserva(String idReserva);
    
}
