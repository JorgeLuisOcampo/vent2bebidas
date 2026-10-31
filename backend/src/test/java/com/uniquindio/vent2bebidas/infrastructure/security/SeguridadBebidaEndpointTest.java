package com.uniquindio.vent2bebidas.infrastructure.security;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Se levanta la aplicación COMPLETA para probar la SecurityFilterChain REAL (la de SecurityConfig).
// jwt() simula un usuario ya autenticado con los roles que le digamos, sin pasar por el login.

@SpringBootTest
@AutoConfigureMockMvc
class SeguridadBebidaEndpointTest {
    @Autowired
    private MockMvc mockMvc;
    @Test
    void sinTokenDeberiaResponder401() throws Exception {
// Act & Assert: no sabemos quién eres
        mockMvc.perform(get("/api/bebidas"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void conRolIncorrectoDeberiaResponder403() throws Exception {
// Act & Assert: sabemos quién eres (COMPRADOR) pero publicar es solo de VENDEDOR
        mockMvc.perform(post("/api/bebidas")
                        .with(jwt().authorities(new SimpleGrantedAuthority("COMPRADOR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }
    @Test
    void conRolCorrectoDeberiaResponder200() throws Exception {
// Act & Assert: cualquier usuario autenticado puede ver el catálogo
        mockMvc.perform(get("/api/bebidas")
                        .with(jwt().authorities(new SimpleGrantedAuthority("COMPRADOR"))))
                .andExpect(status().isOk());
    }
}