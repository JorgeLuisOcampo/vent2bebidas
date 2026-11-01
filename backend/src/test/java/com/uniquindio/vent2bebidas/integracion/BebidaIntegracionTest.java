package com.uniquindio.vent2bebidas.integracion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// PRUEBA DE INTEGRACIÓN: no hay ningún @MockitoBean.
// La petición recorre controller -> caso de uso -> repositorio JPA -> base H2 de verdad.
// @Transactional deshace lo guardado al terminar. Depende de los datos de data.sql.

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BebidaIntegracionTest {
    private static final UUID VENDEDOR_ID =
            UUID.fromString("a1111111-1111-1111-1111-111111111111");
    private static final String CERVEZA_RUBIA_ID =
            "b0000000-0000-0000-0000-000000000001";
    @Autowired
    private MockMvc mockMvc;
    @Test
    void deberiaPublicarUnaBebidaYPoderConsultarlaDespues() throws Exception {
// Arrange
        String cuerpo = """
{
"nombre": "Cerveza de Integracion",
"material": "VIDRIO",
"capacidadMl": 330,
"retornable": true,
"grupoAlcoholico": "FERMENTADA",
"monto": 5200,
"moneda": "COP"
}
""";
// Act: publicar (el vendedor sale del claim "uid" del token)
        MvcResult creada = mockMvc.perform(post("/api/bebidas")
                        .with(jwt().jwt(j -> j.claim("uid", VENDEDOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority("VENDEDOR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PUBLICADA"))
                .andExpect(jsonPath("$.vendedorId").value(VENDEDOR_ID.toString()))
                .andReturn();
        String location = creada.getResponse().getHeader("Location");
        assertNotNull(location);
// Assert: la bebida quedó guardada de verdad y se puede consultar
        mockMvc.perform(get(location)
                        .with(jwt().authorities(new SimpleGrantedAuthority("COMPRADOR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Cerveza de Integracion"))
                .andExpect(jsonPath("$.monto").value(5200.0));
    }
    @Test
    void deberiaCambiarElPrecioDeUnaBebidaDeDataSqlYVerloReflejado() throws Exception
    {
// Arrange
        String cuerpo = """
{ "monto": 5600, "moneda": "COP" }
""";
// Act
        mockMvc.perform(put("/api/bebidas/{id}/precio", CERVEZA_RUBIA_ID)
                        // el vendedor del token es el DUEÑO de la bebida en data.sql
                        .with(jwt().jwt(j -> j.claim("uid", VENDEDOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority("VENDEDOR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isNoContent());
// Assert: el cambio llegó hasta la base de datos
        mockMvc.perform(get("/api/bebidas/{id}", CERVEZA_RUBIA_ID)
                        .with(jwt().authorities(new SimpleGrantedAuthority("COMPRADOR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monto").value(5600.0));
    }
}
