/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.exceptions;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Raú
 */
@ControllerAdvice
public class GlobalErrorHandler {

    /**
     * Maneja errores de validación a nivel global
     * @param ex La excepción lanzada
     * @param bindingResult El resultado de la validación
     * @param redirectAttributes Atributos para pasar mensajes de error a la vista
     * @return Redirige al formulario con los errores
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationErrors(MethodArgumentNotValidException ex, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // Obtener los errores de validación
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            bindingResult.rejectValue(error.getObjectName(), error.getCode(), error.getDefaultMessage());
        }
        
        // Redirigir de vuelta al formulario con los errores
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.client", bindingResult);
        redirectAttributes.addFlashAttribute("client", ex.getBindingResult().getTarget());
        
        return "redirect:/clients/crear_client"; // Redirige a la página de creación de cliente
    }

    /**
     * Maneja errores de recursos duplicados (como el DNI o el correo)
     * @param ex La excepción lanzada
     * @param redirectAttributes Atributos para pasar los mensajes de error
     * @return Redirige al formulario con los errores
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public String handleDuplicateResourceError(DuplicateResourceException ex, RedirectAttributes redirectAttributes) {
        // Añadir el mensaje de error de la excepción
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());

        // Redirige al formulario con los errores de duplicación
        return "redirect:/clients/crear_client"; // O la página de modificación si fuera necesario
    }
}