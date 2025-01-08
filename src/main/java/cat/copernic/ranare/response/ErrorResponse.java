/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.response;

import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.ObjectError;

/**
 *
 * @author Raú
 */
public class ErrorResponse {

    private List<String> errorMessages;

    public ErrorResponse(List<ObjectError> errors) {
        this.errorMessages = new ArrayList<>();
        for (ObjectError error : errors) {
            this.errorMessages.add(error.getDefaultMessage());
        }
    }

    public ErrorResponse(String errorMessage) {
        this.errorMessages = new ArrayList<>();
        this.errorMessages.add(errorMessage);
    }

    // Getter para obtener los mensajes de error
    public List<String> getErrorMessages() {
        return errorMessages;
    }

    // Setter para establecer los mensajes de error
    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    // Método para agregar un solo mensaje de error
    public void addError(String message) {
        this.errorMessages.add(message);
    }
}