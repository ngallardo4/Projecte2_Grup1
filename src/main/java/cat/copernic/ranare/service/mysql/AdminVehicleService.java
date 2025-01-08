    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    package cat.copernic.ranare.service.mysql;

    import cat.copernic.ranare.entity.mysql.Vehicle;
    import cat.copernic.ranare.repository.mysql.VehicleRepository;
    import java.util.List;
    import java.util.Optional;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    /**
     *
     * @author ngall
     */
    @Service
    public class AdminVehicleService {

        @Autowired
        private VehicleRepository vehicleRepository;

        public Vehicle saveVehicle(Vehicle vehicle){
            return vehicleRepository.save(vehicle);
        }

        public void deleteVehicle(String matricula){
            vehicleRepository.deleteById(matricula);
        }

        @Transactional
        public void deleteVehiclesByIds(List<String> matriculas) {
            vehicleRepository.deleteAllById(matriculas);
        }

        public List<Vehicle> getAllVehicles(){
            return vehicleRepository.findAll();
        }

        public Vehicle getVehicleByMatricula(String matricula){
            Optional<Vehicle> vehicle = vehicleRepository.findById(matricula);
            return vehicle.orElse(null);
        }
    }
