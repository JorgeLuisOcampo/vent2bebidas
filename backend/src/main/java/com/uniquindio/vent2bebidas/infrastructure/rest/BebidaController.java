package com.uniquindio.vent2bebidas.infrastructure.rest;

import com.uniquindio.vent2bebidas.application.dto.request.CambiarPrecioRequest;
import com.uniquindio.vent2bebidas.application.dto.request.RegistrarBebidaRequest;
import com.uniquindio.vent2bebidas.application.dto.response.BebidaDetalleResponse;
import com.uniquindio.vent2bebidas.application.dto.response.PageResponse;
import com.uniquindio.vent2bebidas.application.usecase.BuscarBebidasUseCase;
import com.uniquindio.vent2bebidas.application.usecase.CambiarPrecioBebidaUseCase;
import com.uniquindio.vent2bebidas.application.usecase.ObtenerBebidaUseCase;
import com.uniquindio.vent2bebidas.application.usecase.RegistrarBebidaUseCase;
import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import com.uniquindio.vent2bebidas.infrastructure.rest.mapper.BebidaMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/bebidas")
public class BebidaController {
    private final BuscarBebidasUseCase buscarBebidasUseCase;
    private final ObtenerBebidaUseCase obtenerBebidaUseCase;
    private final RegistrarBebidaUseCase registrarBebidaUseCase;
    private final CambiarPrecioBebidaUseCase cambiarPrecioUseCase;
    private final BebidaMapper mapper;

    public BebidaController(BuscarBebidasUseCase buscarBebidasUseCase,
                            ObtenerBebidaUseCase obtenerBebidaUseCase,
                            RegistrarBebidaUseCase registrarBebidaUseCase,
                            CambiarPrecioBebidaUseCase cambiarPrecioUseCase,
                            BebidaMapper mapper) {
        this.buscarBebidasUseCase = buscarBebidasUseCase;
        this.obtenerBebidaUseCase = obtenerBebidaUseCase;
        this.registrarBebidaUseCase = registrarBebidaUseCase;
        this.cambiarPrecioUseCase = cambiarPrecioUseCase;
        this.mapper = mapper;
    }

    // GET /api/bebidas?texto=cerveza&grupo=FERMENTADA&pagina=0&tamano=10
    @GetMapping
    public PageResponse<BebidaDetalleResponse> buscar(
            @RequestParam(name = "texto", required = false) String texto,
            @RequestParam(name = "grupo", required = false) GrupoAlcoholico grupo,
            @RequestParam(name = "pagina", defaultValue = "0") int pagina,
            @RequestParam(name = "tamano", defaultValue = "10") int tamano) {
        return PageResponse.desde(buscarBebidasUseCase.ejecutar(texto, grupo, pagina,
                tamano), mapper::toResponse);
    }

    // GET /api/bebidas/{id}
    @GetMapping("/{id}")
    public BebidaDetalleResponse obtener(@PathVariable("id") UUID id) {
        return mapper.toResponse(obtenerBebidaUseCase.ejecutar(id));
    }

    // POST /api/bebidas (solo VENDEDOR). El vendedor sale del claim "uid" del token.
    @PostMapping
    public ResponseEntity<BebidaDetalleResponse> registrar(@Valid @RequestBody
                                                           RegistrarBebidaRequest request,
                                                           @AuthenticationPrincipal Jwt jwt) {
        UUID vendedorId = vendedorIdDe(jwt);
        Bebida bebida = registrarBebidaUseCase.ejecutar(
                vendedorId,
                request.nombre(),
                mapper.toTipoEnvase(request),
                request.grupoAlcoholico(),
                mapper.toPrecio(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(bebida.getId())
                .toUri();
        return ResponseEntity.created(location).body(mapper.toResponse(bebida));
    }

    // PUT /api/bebidas/{id}/precio (solo VENDEDOR)
    @PutMapping("/{id}/precio")
    public ResponseEntity<Void> cambiarPrecio(@PathVariable("id") UUID id,
                                              @Valid @RequestBody CambiarPrecioRequest request) {
        cambiarPrecioUseCase.ejecutar(id, mapper.toPrecio(request));
        return ResponseEntity.noContent().build();
    }

    private UUID vendedorIdDe(Jwt jwt) {
        String uid = (jwt == null) ? null : jwt.getClaimAsString("uid");
        if (uid == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El token no incluye el claim 'uid'.");
        }
        try {
            return UUID.fromString(uid);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El claim 'uid' no es un UUID válido.");
        }
    }
}