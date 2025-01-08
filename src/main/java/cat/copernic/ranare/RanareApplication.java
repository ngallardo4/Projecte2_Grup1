package cat.copernic.ranare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class RanareApplication {

	public static void main(String[] args) {
		SpringApplication.run(RanareApplication.class, args);
                
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Contraseña sin cifrar
        String rawPassword = "adminpassword";  // Cambia esto a la contraseña que quieras usar
        
        // Generar el hash
        String encodedPassword = encoder.encode(rawPassword);
        
        // Imprimir el hash resultante
        System.out.println("Encoded password: " + encodedPassword);
	}

}
