package com.uniquindio.vent2bebidas.infrastructure.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

// Roles del sistema: COMPRADOR y VENDEDOR (vienen en el claim "roles" del token, SIN prefijo "ROLE_").
@Configuration
public class SecurityConfig {
    private static final String COMPRADOR = "COMPRADOR";
    private static final String VENDEDOR = "VENDEDOR";
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // API sin sesión ni cookies: no aplica CSRF
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // la consola de H2 se dibuja dentro de un <frame>: se permite solo desde el mismo origen
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
// documentación y consola de la base: públicas
                        .requestMatchers("/error", "/v3/api-docs/**", "/swagger-ui/**",
                                "/swagger-ui.html", "/h2-console/**").permitAll()
// ver el catálogo: cualquier usuario autenticado
                        .requestMatchers(HttpMethod.GET, "/api/bebidas", "/api/bebidas/**")
                        .hasAnyAuthority(COMPRADOR, VENDEDOR)
// publicar y cambiar precios: solo vendedores
                        .requestMatchers(HttpMethod.POST,
                                "/api/bebidas").hasAuthority(VENDEDOR)
                        .requestMatchers(HttpMethod.PUT,
                                "/api/bebidas/*/precio").hasAuthority(VENDEDOR)
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }
    // El JwtDecoder que verifica la firma ahora vive en JwtConfig (junto con el JwtEncoder).
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
