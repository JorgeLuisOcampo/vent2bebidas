package com.uniquindio.vent2bebidas.infrastructure.security;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

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
                .authorizeHttpRequests(auth -> auth
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
    // Verifica la firma de los tokens que llegan en "Authorization: Bearer <token>".
    @Bean
    JwtDecoder jwtDecoder(@Value("${app.jwt.secret}") String secreto) {
        SecretKey clave = new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256");
        return
                NimbusJwtDecoder.withSecretKey(clave).macAlgorithm(MacAlgorithm.HS256).build();
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