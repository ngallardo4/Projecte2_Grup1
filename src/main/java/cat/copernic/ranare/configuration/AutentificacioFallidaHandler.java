/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.configuration;

import cat.copernic.ranare.exceptions.UsuariNoActivatException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 *
 * @author reyes
 */
public class AutentificacioFallidaHandler implements AuthenticationFailureHandler {

    
    private static final Logger logger = LoggerFactory.getLogger(AutentificacioFallidaHandler.class);
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if(exception instanceof InternalAuthenticationServiceException){
            if(exception.getCause() instanceof UsuariNoActivatException){
                logger.info("Redirigiendo a /public/login?error=desactivat");
                response.sendRedirect("/public/login?error=desactivat");
            }else{
                logger.info("Redirigiendo a /public/login?error");
                response.sendRedirect("/public/login?error");
            }
            
        }else{
            logger.info("Redirigiendo a /public/login?error");
            response.sendRedirect("/public/login?error");
        }
            
    }
    
}
