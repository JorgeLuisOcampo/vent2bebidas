package com.uniquindio.vent2bebidas.infrastructure.security;

import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.UsuarioJpaDataRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Piezas que usa el LOGIN: de dónde salen los usuarios y cómo se comparan las contraseñas.
@Configuration
public class AutenticacionConfig {

    // Busca el usuario por correo en la base de datos.
    @Bean
    public UserDetailsService userDetailsService(UsuarioJpaDataRepository repository) {
        return email -> repository.findByEmail(email.trim().toLowerCase())
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no existe"));
    }

    // BCrypt: hash lento y con "sal" aleatoria. Aunque roben la base, no se puede recuperar la contraseña,
    // y dos usuarios con la misma contraseña quedan con hashes distintos.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Spring arma el AuthenticationManager con el UserDetailsService y el PasswordEncoder de arriba.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}