/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare;

import cat.copernic.ranare.entity.mysql.Localitzacio;
import cat.copernic.ranare.exceptions.InvalidCodiPostalException;
import cat.copernic.ranare.repository.mysql.LocalitzacioRepository;
import cat.copernic.ranare.service.mysql.LocalitzacioService;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 *
 * @author reyes
 */
@ExtendWith(MockitoExtension.class)
public class LocalitzacioServiceTest {
    
    @Mock
    private LocalitzacioRepository localitzacioRepository;

    @InjectMocks
    private LocalitzacioService localitzacioService;

    @Test
    void testSaveLocalitzacioValid() {
        // Arrange
        Localitzacio localitzacio = new Localitzacio("08001", "Carrer X", "Barcelona", "Espanya", "Carrer", 
                                                      LocalTime.of(8, 0), LocalTime.of(20, 0), null, null);
        when(localitzacioRepository.save(any(Localitzacio.class))).thenReturn(localitzacio);

        // Act
        Localitzacio result = localitzacioService.saveLocalitzacio(localitzacio);

        // Assert
        assertEquals("08001", result.getCodiPostal());
        verify(localitzacioRepository, times(1)).save(localitzacio);
    }

    @Test
    void testSaveLocalitzacioInvalidCodiPostal() {
        // Arrange
        Localitzacio localitzacio = new Localitzacio("0800A", "Carrer X", "Barcelona", "Espanya", "Carrer", 
                                                      LocalTime.of(8, 0), LocalTime.of(20, 0), null, null);

        // Act & Assert
        InvalidCodiPostalException exception = assertThrows(
            InvalidCodiPostalException.class, 
            () -> localitzacioService.saveLocalitzacio(localitzacio)
        );
        assertEquals("El codi postal ha de contenir només números.", exception.getMessage());
        verify(localitzacioRepository, never()).save(any(Localitzacio.class));
    }
}
