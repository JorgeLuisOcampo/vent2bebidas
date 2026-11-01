package com.uniquindio.vent2bebidas.infrastructure.rest;

import com.uniquindio.vent2bebidas.application.dto.response.ConsumidorResponse;
import com.uniquindio.vent2bebidas.application.usecase.ObtenerConsumidorUseCase;
import com.uniquindio.vent2bebidas.infrastructure.rest.mapper.ConsumidorMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/consumidores")
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
    public ConsumidorResponse miPerfil() {
        return mapper.toResponse(obtenerConsumidorUseCase.ejecutar(usuarioAutenticado.id()));
    }
}
