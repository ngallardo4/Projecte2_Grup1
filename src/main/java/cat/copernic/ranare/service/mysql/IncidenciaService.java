/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.service.mysql;

import cat.copernic.ranare.entity.mysql.Incidencia;
import cat.copernic.ranare.repository.mysql.IncidenciaRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ngall
 */
@Service
public class IncidenciaService {
    
    @Autowired
    private IncidenciaRepository incidenciaRepository;
    
    public List<Incidencia> findAll(){
        return incidenciaRepository.findAll();
    }
    
    public Incidencia findById(Long id){
        return incidenciaRepository.findById(id).orElse(null);
    }
    
    public List<Incidencia> findByVehicleMatricula(String matricula){
        return incidenciaRepository.findByVehicle_Matricula(matricula);
    }
    
    public Incidencia save(Incidencia incidencia){
        return incidenciaRepository.save(incidencia);
    }
    
    public void deleteById(Long id){
        incidenciaRepository.deleteById(id);
    }
}
