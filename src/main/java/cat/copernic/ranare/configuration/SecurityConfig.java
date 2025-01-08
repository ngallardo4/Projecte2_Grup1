/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.ranare.configuration;

import cat.copernic.ranare.entity.mysql.Agent;
import cat.copernic.ranare.entity.mysql.Client;
import cat.copernic.ranare.enums.Reputacio;
import cat.copernic.ranare.enums.Rol;
import cat.copernic.ranare.repository.mysql.AgentRepository;
import cat.copernic.ranare.repository.mysql.ClientRepository;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 *
 * @author reyes
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private ValidadorUsuaris validador;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Bean
    public CommandLineRunner initData(PasswordEncoder passwordEncoder) {
        return args -> {
            //crear un admin si no existeix
            if (!agentRepository.existsByUsername("admin")) {
                Agent admin = Agent.builder()
                        .dni("12345678A")
                        .nom("Admin")
                        .cognoms("Administrador")
                        .nacionalitat("Espanyola")
                        .telefon("600000001")
                        .username("admin")
                        .pwd(passwordEncoder.encode("admin123"))
                        .email("admin@example.com")
                        .adreca("Carrer Admin 1")
                        .pais("Espanya")
                        .ciutat("Barcelona")
                        .codiPostal("08001")
                        .numeroTarjetaCredit("1111222233334444")
                        .reputacio(Reputacio.NORMAL)
                        .actiu(true)
                        .dataNaixement(LocalDate.of(1990, 1, 1))
                        .rol(Rol.ADMIN)
                        .build();
                agentRepository.save(admin);
            }

            //crear agent de prova si no existeix
            if (!agentRepository.existsByUsername("agent")) {
                Agent agent = Agent.builder()
                        .dni("12345678B")
                        .nom("Agent")
                        .cognoms("Agente")
                        .nacionalitat("Espanyola")
                        .telefon("600000002")
                        .username("agent")
                        .pwd(passwordEncoder.encode("agent123"))
                        .email("agent@example.com")
                        .adreca("Carrer Agent 1")
                        .pais("Espanya")
                        .ciutat("Valencia")
                        .codiPostal("46001")
                        .numeroTarjetaCredit("5555666677778888")
                        .reputacio(Reputacio.PREMIUM)
                        .actiu(true)
                        .dataNaixement(LocalDate.of(1995, 5, 15))
                        .rol(Rol.AGENT)
                        .build();
                agentRepository.save(agent);
            }

            //crear client de prova si no existeix
            if (!clientRepository.existsByUsername("client")) {
                Client client = Client.builder()
                        .dni("12345678C")
                        .nom("Client")
                        .cognoms("Cliente")
                        .nacionalitat("Espanyola")
                        .telefon("600000003")
                        .username("client")
                        .pwd(passwordEncoder.encode("client123"))
                        .email("client@example.com")
                        .adreca("Carrer Client 1")
                        .pais("Espanya")
                        .ciutat("Madrid")
                        .codiPostal("28001")
                        .numeroTarjetaCredit("9999000011112222")
                        .reputacio(Reputacio.NORMAL)
                        .actiu(true)
                        .dataNaixement(LocalDate.of(2000, 7, 20))
                        .build();
                clientRepository.save(client);
            }
        };
    }

    @Bean
    public AuthenticationFailureHandler autentificacioPersonalitzadaFallida() {
        return new AutentificacioFallidaHandler();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Para cifrar las contraseñas
    }

    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(requests -> requests
            // Rutas administrativas
            .requestMatchers("/admin/clients/inactius").hasRole("ADMIN")
            .requestMatchers("/admin/clients/**").hasAnyRole("ADMIN", "AGENT")
            .requestMatchers("/admin/vehicles/**").hasAnyRole("ADMIN", "AGENT")
            .requestMatchers("/admin/localitzacio/**").hasRole("ADMIN")
            .requestMatchers("/admin/agents/**").hasRole("ADMIN")
            
            // Rutas públicas que requieren autenticación
            .requestMatchers("/public/usuari/detalls", "/public/usuari/update", "/public/usuari/eliminar", "/public/usuari/reserves/**").authenticated()

            // Rutas completamente públicas
            .requestMatchers("/public/login", "/public/registre/**", "/public/").permitAll()
            
            // Cualquier otra ruta requiere autenticación
            .anyRequest().authenticated()
    )
    .formLogin(form -> form
            .loginPage("/public/login")
            .loginProcessingUrl("/public/login")
            .defaultSuccessUrl("/public", true)
            .failureHandler(autentificacioPersonalitzadaFallida())
            .permitAll()
    )
    .logout(logout -> logout
            .logoutUrl("/public/logout")
            .logoutSuccessUrl("/public/login?logout=true")
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .permitAll()
    )
    .exceptionHandling(exception -> exception.authenticationEntryPoint(new AutentificacioEntrada()));

    return http.build();
}
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(validador).and().build();
    }

}
