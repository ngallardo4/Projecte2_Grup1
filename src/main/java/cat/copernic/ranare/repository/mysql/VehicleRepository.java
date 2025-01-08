/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.repository.mysql;

import cat.copernic.ranare.entity.mysql.Vehicle;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ngall
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {
    @Query(value = "SELECT * FROM Vehicle ORDER BY RAND() LIMIT 3", nativeQuery = true)
    List<Vehicle> findRandomVehicles();
}
