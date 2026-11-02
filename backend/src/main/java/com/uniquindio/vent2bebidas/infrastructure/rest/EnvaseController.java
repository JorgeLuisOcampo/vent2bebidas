package com.uniquindio.vent2bebidas.infrastructure.rest;

import com.uniquindio.vent2bebidas.application.dto.request.RegistrarEnvaseRequest;
import com.uniquindio.vent2bebidas.application.dto.response.EnvaseDetalleResponse;
import com.uniquindio.vent2bebidas.application.usecase.DescartarEnvaseUseCase;
import com.uniquindio.vent2bebidas.application.usecase.MarcarEnvaseComoDanadoUseCase;
import com.uniquindio.vent2bebidas.application.usecase.ObtenerEnvaseUseCase;
import com.uniquindio.vent2bebidas.application.usecase.RegistrarEnvaseUseCase;
import com.uniquindio.vent2bebidas.application.usecase.RegistrarRetornoEnvaseUseCase;
import com.uniquindio.vent2bebidas.domain.entity.Envase;
import com.uniquindio.vent2bebidas.infrastructure.rest.mapper.EnvaseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

// Controller DELGADO del ciclo de vida de los envases retornables (rol VENDEDOR).
@RestController
@RequestMapping("/api/envases")
@Tag(name = "Envases", description = "Ciclo de vida de los envases retornables (VENDEDOR)")
public class EnvaseController {

    private final RegistrarEnvaseUseCase registrarEnvaseUseCase;
    private final ObtenerEnvaseUseCase obtenerEnvaseUseCase;
    private final RegistrarRetornoEnvaseUseCase registrarRetornoUseCase;
    private final MarcarEnvaseComoDanadoUseCase marcarDanadoUseCase;
    private final DescartarEnvaseUseCase descartarUseCase;
    private final EnvaseMapper mapper;

    public EnvaseController(RegistrarEnvaseUseCase registrarEnvaseUseCase,
                            ObtenerEnvaseUseCase obtenerEnvaseUseCase,
                            RegistrarRetornoEnvaseUseCase registrarRetornoUseCase,
                            MarcarEnvaseComoDanadoUseCase marcarDanadoUseCase,
                            DescartarEnvaseUseCase descartarUseCase,
                            EnvaseMapper mapper) {
        this.registrarEnvaseUseCase = registrarEnvaseUseCase;
        this.obtenerEnvaseUseCase = obtenerEnvaseUseCase;
        this.registrarRetornoUseCase = registrarRetornoUseCase;
        this.marcarDanadoUseCase = marcarDanadoUseCase;
        this.descartarUseCase = descartarUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Registrar un envase (VENDEDOR)",
            description = "422 si se intenta registrar un envase de aluminio como retornable.")
    public ResponseEntity<EnvaseDetalleResponse> registrar(@Valid @RequestBody RegistrarEnvaseRequest request) {
        Envase envase = registrarEnvaseUseCase.ejecutar(mapper.toTipoEnvase(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(envase.getId())
                .toUri();
        return ResponseEntity.created(location).body(mapper.toResponse(envase));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un envase (VENDEDOR)", description = "Incluye los usos restantes.")
    public EnvaseDetalleResponse obtener(@PathVariable("id") UUID id) {
        return mapper.toResponse(obtenerEnvaseUseCase.ejecutar(id));
    }

    @PutMapping("/{id}/retorno")
    @Operation(summary = "Registrar el retorno de un envase (VENDEDOR)",
            description = "Suma un uso. 422 si no es retornable, está dañado, descartado o llegó al límite de usos.")
    public EnvaseDetalleResponse registrarRetorno(@PathVariable("id") UUID id) {
        return mapper.toResponse(registrarRetornoUseCase.ejecutar(id));
    }

    @PutMapping("/{id}/danado")
    @Operation(summary = "Marcar un envase como dañado (VENDEDOR)")
    public EnvaseDetalleResponse marcarDanado(@PathVariable("id") UUID id) {
        return mapper.toResponse(marcarDanadoUseCase.ejecutar(id));
    }

    @PutMapping("/{id}/descarte")
    @Operation(summary = "Descartar un envase (VENDEDOR)",
            description = "Borrado lógico: el envase queda DESCARTADO y ya no puede modificarse.")
    public EnvaseDetalleResponse descartar(@PathVariable("id") UUID id) {
        return mapper.toResponse(descartarUseCase.ejecutar(id));
    }
}