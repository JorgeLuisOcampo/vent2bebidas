package com.uniquindio.vent2bebidas.infrastructure.rest;

import com.uniquindio.vent2bebidas.application.dto.response.ConsumidorResponse;
import com.uniquindio.vent2bebidas.application.usecase.ObtenerConsumidorUseCase;
import com.uniquindio.vent2bebidas.infrastructure.rest.mapper.ConsumidorMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/consumidores")
@Tag(name = "Consumidores", description = "Perfil del consumidor autenticado (COMPRADOR)")
public class ConsumidorController {

    private final ObtenerConsumidorUseCase obtenerConsumidorUseCase;
    private final ConsumidorMapper mapper;
    private final UsuarioAutenticado usuarioAutenticado;

    public ConsumidorController(ObtenerConsumidorUseCase obtenerConsumidorUseCase, ConsumidorMapper mapper,
                                UsuarioAutenticado usuarioAutenticado) {
        this.obtenerConsumidorUseCase = obtenerConsumidorUseCase;
        this.mapper = mapper;
        this.usuarioAutenticado = usuarioAutenticado;
    }

    // GET /api/consumidores/yo  (solo COMPRADOR)
    @GetMapping("/yo")
    @Operation(summary = "Ver mi perfil (COMPRADOR)",
            description = "Incluye si es mayor de edad y su número de compras históricas.")
    public ConsumidorResponse miPerfil() {
        return mapper.toResponse(obtenerConsumidorUseCase.ejecutar(usuarioAutenticado.id()));
    }
}