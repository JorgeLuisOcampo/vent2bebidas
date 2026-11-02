package com.uniquindio.vent2bebidas.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// AQUÍ se definen TODAS las reglas de acceso de la API (quién puede llamar qué).
// Roles del sistema: COMPRADOR y VENDEDOR (vienen en el claim "roles" del token, SIN prefijo "ROLE_").
@Configuration
public class SecurityConfig {
    private static final String COMPRADOR = "COMPRADOR";
    private static final String VENDEDOR = "VENDEDOR";

    private final ManejadorErroresSeguridad manejadorErrores;

    public SecurityConfig(ManejadorErroresSeguridad manejadorErrores) {
        this.manejadorErrores = manejadorErrores;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // API sin sesión ni cookies: no aplica CSRF
                .cors(Customizer.withDefaults())
                // STATELESS: el servidor no guarda sesiones; cada petición trae su token y se valida sola.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // la consola de H2 se dibuja dentro de un <frame>: se permite solo desde el mismo origen
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        // públicas: documentación, consola de la base, login y registro
                        .requestMatchers("/error", "/v3/api-docs/**", "/swagger-ui/**",
                                "/swagger-ui.html", "/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                        // catálogo: cualquier usuario autenticado
                        .requestMatchers(HttpMethod.GET, "/api/bebidas", "/api/bebidas/**")
                        .hasAnyAuthority(COMPRADOR, VENDEDOR)
                        // gestión del catálogo: solo vendedores
                        .requestMatchers(HttpMethod.POST, "/api/bebidas").hasAuthority(VENDEDOR)
                        .requestMatchers(HttpMethod.PUT, "/api/bebidas/*/precio").hasAuthority(VENDEDOR)
                        .requestMatchers(HttpMethod.DELETE, "/api/bebidas/*").hasAuthority(VENDEDOR)
                        // envases y reportes: solo vendedores
                        .requestMatchers("/api/envases", "/api/envases/**").hasAuthority(VENDEDOR)
                        .requestMatchers("/api/reportes/**").hasAuthority(VENDEDOR)
                        // compras y perfil del consumidor: solo compradores
                        .requestMatchers("/api/compras", "/api/compras/**").hasAuthority(COMPRADOR)
                        .requestMatchers("/api/consumidores/**").hasAuthority(COMPRADOR)
                        // cualquier otra ruta exige estar autenticado
                        .anyRequest().authenticated())
                // respuestas JSON para 401 y 403
                .exceptionHandling(errores -> errores
                        .authenticationEntryPoint(manejadorErrores)
                        .accessDeniedHandler(manejadorErrores))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(manejadorErrores)
                        .accessDeniedHandler(manejadorErrores));
        return http.build();
    }

    // Permite que el frontend de Angular (Entrega 3) llame a la API desde otro puerto.
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // Convierte el claim "roles" del token en autoridades de Spring Security (sin prefijo).
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter autoridades = new JwtGrantedAuthoritiesConverter();
        autoridades.setAuthoritiesClaimName("roles");
        autoridades.setAuthorityPrefix("");
        JwtAuthenticationConverter convertidor = new JwtAuthenticationConverter();
        convertidor.setJwtGrantedAuthoritiesConverter(autoridades);
        return convertidor;
    }
}