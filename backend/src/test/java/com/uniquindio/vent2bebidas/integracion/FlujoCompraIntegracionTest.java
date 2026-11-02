package com.uniquindio.vent2bebidas.integracion;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// FLUJO DE PUNTA A PUNTA sin simular nada: login real -> controller -> caso de uso -> dominio -> JPA -> H2.
// Es el mismo recorrido que se muestra en vivo en el video (Bloque 7).
// @Transactional deshace todo al terminar.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FlujoCompraIntegracionTest {

    private static final String RUBIA = "b0000000-0000-0000-0000-000000000001";
    private static final String JUGO = "b0000000-0000-0000-0000-000000000007";

    @Autowired
    private MockMvc mockMvc;

    private String tokenDe(String email, String clave) throws Exception {
        String respuesta = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"%s\", \"password\": \"%s\" }".formatted(email, clave)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.read(respuesta, "$.token");
    }

    private String compra(String bebidaId, int unidades) {
        return "{ \"bebidaId\": \"%s\", \"unidades\": %d }".formatted(bebidaId, unidades);
    }

    @Test
    void vendedorPublicaYCompradorCompraYCancela() throws Exception {
        // 1. El vendedor inicia sesión y publica una bebida
        String vendedor = tokenDe("cerveceria@vent2bebidas.com", "Vendedor123*");
        String publicada = mockMvc.perform(post("/api/bebidas")
                        .header("Authorization", vendedor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "nombre": "Cerveza de Flujo", "material": "VIDRIO", "capacidadMl": 330,
                                  "retornable": true, "grupoAlcoholico": "FERMENTADA",
                                  "monto": 7000, "moneda": "COP" }
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String bebidaId = JsonPath.read(publicada, "$.id");

        // 2. La compradora (mayor de edad) inicia sesión y compra 2 unidades
        String ana = tokenDe("ana@vent2bebidas.com", "Comprador123*");
        String comprada = mockMvc.perform(post("/api/compras")
                        .header("Authorization", ana)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(compra(bebidaId, 2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(14000.0))
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"))
                .andReturn().getResponse().getContentAsString();
        String compraId = JsonPath.read(comprada, "$.id");

        // 3. Su historial de compras subió (tenía 5 en data.sql)
        mockMvc.perform(get("/api/consumidores/yo").header("Authorization", ana))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comprasHistoricas").value(6));

        // 4. La cancela dentro de las 24 horas
        mockMvc.perform(put("/api/compras/{id}/cancelar", compraId).header("Authorization", ana))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));

        // 5. Cancelarla otra vez rompe una regla de negocio -> 422
        mockMvc.perform(put("/api/compras/{id}/cancelar", compraId).header("Authorization", ana))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.mensaje").value("La compra ya está cancelada."));
    }

    @Test
    void lasReglasDeNegocioSeRespetanDesdeLaApi() throws Exception {
        // Una menor de edad intenta comprar cerveza -> 422 (RN-08)
        String sofia = tokenDe("sofia@vent2bebidas.com", "Comprador123*");
        mockMvc.perform(post("/api/compras")
                        .header("Authorization", sofia)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(compra(RUBIA, 1)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.mensaje").value("Un menor de edad no puede comprar bebidas alcohólicas."));

        // Carlos (3 compras) pide 6 unidades -> 422 (RN-09)
        String carlos = tokenDe("carlos@vent2bebidas.com", "Comprador123*");
        mockMvc.perform(post("/api/compras")
                        .header("Authorization", carlos)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(compra(JUGO, 6)))
                .andExpect(status().is(422));

        // Un vendedor intenta cambiar el precio de una bebida que NO es suya -> 422 (RN-11)
        String otroVendedor = tokenDe("licores@vent2bebidas.com", "Vendedor123*");
        mockMvc.perform(put("/api/bebidas/{id}/precio", RUBIA)
                        .header("Authorization", otroVendedor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"monto\": 9000, \"moneda\": \"COP\" }"))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.mensaje").value("Solo el vendedor que publicó la bebida puede modificarla."));
    }

    @Test
    void elRankingLoCalculaLaBaseDeDatosConLosDatosDePrueba() throws Exception {
        // Arrange
        String vendedor = tokenDe("licores@vent2bebidas.com", "Vendedor123*");

        // Act & Assert: Rubia = 6 + 4 + 3 = 13 unidades en 3 compras (data.sql)
        mockMvc.perform(get("/api/reportes/bebidas-mas-vendidas").param("limite", "3")
                        .header("Authorization", vendedor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].posicion").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Cerveza Rubia Artesanal"))
                .andExpect(jsonPath("$[0].unidadesVendidas").value(13))
                .andExpect(jsonPath("$[0].numeroCompras").value(3))
                .andExpect(jsonPath("$[1].nombre").value("Jugo de Naranja"));
    }
}