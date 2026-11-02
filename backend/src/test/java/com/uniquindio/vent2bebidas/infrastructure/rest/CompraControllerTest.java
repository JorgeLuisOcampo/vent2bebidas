package com.uniquindio.vent2bebidas.infrastructure.rest;

import com.uniquindio.vent2bebidas.application.usecase.CancelarCompraUseCase;
import com.uniquindio.vent2bebidas.application.usecase.ConfirmarCompraDeBebidaUseCase;
import com.uniquindio.vent2bebidas.application.usecase.ListarComprasDelConsumidorUseCase;
import com.uniquindio.vent2bebidas.domain.entity.Compra;
import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;
import com.uniquindio.vent2bebidas.infrastructure.rest.mapper.CompraMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Solo levanta la capa web: los casos de uso se SIMULAN con @MockitoBean.
// addFilters = false apaga los filtros de seguridad; el usuario del token se simula con @MockitoBean.
@WebMvcTest(CompraController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CompraMapper.class)
class CompraControllerTest {

    private static final UUID CONSUMIDOR_ID = UUID.randomUUID();
    private static final UUID BEBIDA_ID = UUID.randomUUID();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConfirmarCompraDeBebidaUseCase confirmarCompra;
    @MockitoBean
    private CancelarCompraUseCase cancelarCompra;
    @MockitoBean
    private ListarComprasDelConsumidorUseCase listarCompras;
    // Reemplaza la lectura del token: en esta prueba la seguridad está apagada.
    @MockitoBean
    private UsuarioAutenticado usuarioAutenticado;

    @BeforeEach
    void simularUsuarioDelToken() {
        when(usuarioAutenticado.id()).thenReturn(CONSUMIDOR_ID);
    }

    private String cuerpo(int unidades) {
        return """
                { "bebidaId": "%s", "unidades": %d }
                """.formatted(BEBIDA_ID, unidades);
    }

    @Test
    void deberiaResponder201ConLaCompraYSuLocation() throws Exception {
        // Arrange
        Compra compra = Compra.confirmar(UUID.randomUUID(), CONSUMIDOR_ID, BEBIDA_ID, UUID.randomUUID(),
                3, new Precio(5000, "COP"), LocalDateTime.now());
        when(confirmarCompra.ejecutar(CONSUMIDOR_ID, BEBIDA_ID, 3)).thenReturn(compra);

        // Act & Assert
        mockMvc.perform(post("/api/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(3)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.total").value(15000.0))
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));
    }

    @Test
    void deberiaResponder400YNoLlamarAlCasoDeUsoSiLosDatosSonInvalidos() throws Exception {
        // Act & Assert: sin bebidaId y con 0 unidades
        mockMvc.perform(post("/api/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"unidades\": 0 }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.bebidaId").exists())
                .andExpect(jsonPath("$.campos.unidades").exists());
        verify(confirmarCompra, never()).ejecutar(any(), any(), anyInt());
    }

    @Test
    void deberiaResponder422CuandoUnMenorCompraAlcohol() throws Exception {
        // Arrange: el dominio rechaza la compra
        when(confirmarCompra.ejecutar(CONSUMIDOR_ID, BEBIDA_ID, 1))
                .thenThrow(new ReglaDominioException("Un menor de edad no puede comprar bebidas alcohólicas."));

        // Act & Assert
        mockMvc.perform(post("/api/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(1)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.mensaje").value("Un menor de edad no puede comprar bebidas alcohólicas."));
    }

    @Test
    void deberiaResponder404AlCancelarUnaCompraQueNoExiste() throws Exception {
        // Arrange
        UUID compraId = UUID.randomUUID();
        when(cancelarCompra.ejecutar(eq(compraId), any())).thenThrow(new NoSuchElementException());

        // Act & Assert
        mockMvc.perform(put("/api/compras/{id}/cancelar", compraId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}