package com.uniquindio.vent2bebidas.infrastructure.security;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Prueba el LOGIN REAL: usuarios de UsuariosIniciales, contraseñas BCrypt y tokens firmados de verdad.
// @Transactional deshace los registros que se hagan en cada prueba.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AutenticacionIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    private String login(String email, String clave) throws Exception {
        String respuesta = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "%s", "password": "%s" }
                                """.formatted(email, clave)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(respuesta, "$.token");
    }

    @Test
    void loginCorrectoDevuelveUnTokenBearerConElRol() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"ana@vent2bebidas.com\", \"password\": \"Comprador123*\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.expiraEn").exists())
                .andExpect(jsonPath("$.roles[0]").value("COMPRADOR"));
    }

    @Test
    void loginConContrasenaIncorrectaResponde401() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"ana@vent2bebidas.com\", \"password\": \"otra-clave\" }"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Correo o contraseña incorrectos."));
    }

    @Test
    void elTokenRealDelLoginAbreLaRutaDeSuRolYCierraLaDelOtro() throws Exception {
        // Arrange
        String tokenVendedor = login("cerveceria@vent2bebidas.com", "Vendedor123*");
        String tokenComprador = login("ana@vent2bebidas.com", "Comprador123*");

        // Act & Assert: "mis compras" es solo del COMPRADOR
        mockMvc.perform(get("/api/compras/mias")
                        .header("Authorization", "Bearer " + tokenComprador))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/compras/mias")
                        .header("Authorization", "Bearer " + tokenVendedor))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void unTokenAlteradoResponde401() throws Exception {
        // Arrange: se cambia el primer carácter de la FIRMA (la tercera parte del token)
        String token = login("ana@vent2bebidas.com", "Comprador123*");
        int inicioFirma = token.lastIndexOf('.') + 1;
        char cambiado = token.charAt(inicioFirma) == 'A' ? 'B' : 'A';
        String alterado = token.substring(0, inicioFirma) + cambiado + token.substring(inicioFirma + 1);

        // Act & Assert
        mockMvc.perform(get("/api/bebidas").header("Authorization", "Bearer " + alterado))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registrarUnCompradorYLuegoIniciarSesion() throws Exception {
        // Act: registro
        mockMvc.perform(post("/api/auth/registro/comprador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "nuevo@vent2bebidas.com",
                                  "password": "Clave12345",
                                  "nombre": "Cliente Nuevo",
                                  "codigo": "NUE123",
                                  "fechaNacimiento": "1999-05-20"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rol").value("COMPRADOR"))
                .andExpect(jsonPath("$.password").doesNotExist());

        // Assert: ya puede iniciar sesión con la contraseña que registró
        login("nuevo@vent2bebidas.com", "Clave12345");
    }

    @Test
    void registrarConUnCorreoExistenteResponde409() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/registro/vendedor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "licores@vent2bebidas.com", "password": "Clave12345", "nombre": "Copia" }
                                """))
                .andExpect(status().isConflict());
    }
}