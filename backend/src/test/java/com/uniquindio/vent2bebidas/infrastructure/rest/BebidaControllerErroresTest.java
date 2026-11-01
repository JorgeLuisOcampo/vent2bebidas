package com.uniquindio.vent2bebidas.infrastructure.rest;

import com.uniquindio.vent2bebidas.application.usecase.BuscarBebidasUseCase;
import com.uniquindio.vent2bebidas.application.usecase.CambiarPrecioBebidaUseCase;
import com.uniquindio.vent2bebidas.application.usecase.EliminarBebidaUseCase;
import com.uniquindio.vent2bebidas.application.usecase.ObtenerBebidaUseCase;
import com.uniquindio.vent2bebidas.application.usecase.RegistrarBebidaUseCase;
import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.infrastructure.rest.mapper.BebidaMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Solo levanta la capa web. addFilters = false apaga la SEGURIDAD en esta prueba
// (la seguridad se prueba aparte, en SeguridadBebidaEndpointTest).
@WebMvcTest(BebidaController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(BebidaMapper.class)
class BebidaControllerErroresTest {
    private static final UUID VENDEDOR_ID = UUID.randomUUID();
    private static final String PRECIO_VALIDO = """
            { "monto": 6500, "moneda": "COP" }
            """;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private BuscarBebidasUseCase buscarBebidas;
    @MockitoBean
    private ObtenerBebidaUseCase obtenerBebida;
    @MockitoBean
    private RegistrarBebidaUseCase registrarBebida;
    @MockitoBean
    private CambiarPrecioBebidaUseCase cambiarPrecio;
    @MockitoBean
    private EliminarBebidaUseCase eliminarBebida;

    @Test
    void deberiaResponder404CuandoLaBebidaNoExiste() throws Exception {
// Arrange
        UUID id = UUID.randomUUID();
        when(obtenerBebida.ejecutar(id)).thenThrow(new NoSuchElementException());
// Act & Assert
        mockMvc.perform(get("/api/bebidas/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deberiaResponder404AlCambiarElPrecioDeUnaBebidaQueNoExiste() throws
            Exception {
// Arrange
        UUID id = UUID.randomUUID();
        doThrow(new NoSuchElementException()).when(cambiarPrecio).ejecutar(eq(id),
                any(), any());
// Act & Assert
        mockMvc.perform(put("/api/bebidas/{id}/precio", id)
                        .with(jwt().jwt(j -> j.claim("uid", VENDEDOR_ID.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRECIO_VALIDO))
                .andExpect(status().isNotFound());
    }

    @Test
    void deberiaResponder400CuandoSeRompeUnaReglaDeNegocio() throws Exception {
// Arrange
        UUID id = UUID.randomUUID();
        doThrow(new ReglaDominioException("El nuevo precio debe ser diferente al actual."))
                .when(cambiarPrecio).ejecutar(eq(id), any(), any());
// Act & Assert
        mockMvc.perform(put("/api/bebidas/{id}/precio", id)
                        .with(jwt().jwt(j -> j.claim("uid", VENDEDOR_ID.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRECIO_VALIDO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El nuevo precio debe ser diferente al actual."));
    }

    @Test
    void noDeberiaLlamarAlCasoDeUsoSiLosDatosSonInvalidos() throws Exception {
// Arrange
        UUID id = UUID.randomUUID();
// Act & Assert: el cuerpo vacío rompe @Positive y @NotBlank antes de llegar al caso de uso
        mockMvc.perform(put("/api/bebidas/{id}/precio", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
        verify(cambiarPrecio, never()).ejecutar(any(), any(), any());
    }

    @Test
    void deberiaEliminarLogicamenteConElVendedorDelToken() throws Exception {
// Arrange
        UUID id = UUID.randomUUID();
// Act & Assert: responde 204 y el caso de uso recibe el vendedor que viene en el token
        mockMvc.perform(delete("/api/bebidas/{id}", id)
                        .with(jwt().jwt(j -> j.claim("uid", VENDEDOR_ID.toString()))))
                .andExpect(status().isNoContent());
        verify(eliminarBebida).ejecutar(id, VENDEDOR_ID);
    }
}
