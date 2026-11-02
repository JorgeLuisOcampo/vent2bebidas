package com.uniquindio.vent2bebidas.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Las tres pruebas de una ruta protegida (401, 403 y 200) sobre las rutas del COMPRADOR,
// más las rutas exclusivas del VENDEDOR. Se usa la SecurityFilterChain REAL.
@SpringBootTest
@AutoConfigureMockMvc
class SeguridadRolesEndpointTest {

    private static final String ANA = "c1111111-1111-1111-1111-111111111111";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void misComprasSinTokenResponde401EnJson() throws Exception {
        mockMvc.perform(get("/api/compras/mias"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void misComprasConRolVendedorResponde403() throws Exception {
        mockMvc.perform(get("/api/compras/mias")
                        .with(jwt().authorities(new SimpleGrantedAuthority("VENDEDOR"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void misComprasConRolCompradorResponde200() throws Exception {
        mockMvc.perform(get("/api/compras/mias")
                        .with(jwt().jwt(j -> j.claim("uid", ANA))
                                .authorities(new SimpleGrantedAuthority("COMPRADOR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void unCompradorNoPuedeGestionarEnvasesNiEliminarBebidas() throws Exception {
        mockMvc.perform(get("/api/envases/{id}", "e0000000-0000-0000-0000-000000000001")
                        .with(jwt().authorities(new SimpleGrantedAuthority("COMPRADOR"))))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/bebidas/{id}", "b0000000-0000-0000-0000-000000000001")
                        .with(jwt().authorities(new SimpleGrantedAuthority("COMPRADOR"))))
                .andExpect(status().isForbidden());
    }
}