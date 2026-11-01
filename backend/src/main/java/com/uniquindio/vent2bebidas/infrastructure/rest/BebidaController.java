package com.uniquindio.vent2bebidas.infrastructure.rest;

import com.uniquindio.vent2bebidas.application.dto.request.CambiarPrecioRequest;
import com.uniquindio.vent2bebidas.application.dto.request.RegistrarBebidaRequest;
import com.uniquindio.vent2bebidas.application.dto.response.BebidaDetalleResponse;
import com.uniquindio.vent2bebidas.application.dto.response.PageResponse;
import com.uniquindio.vent2bebidas.application.usecase.BuscarBebidasUseCase;
import com.uniquindio.vent2bebidas.application.usecase.CambiarPrecioBebidaUseCase;
import com.uniquindio.vent2bebidas.application.usecase.EliminarBebidaUseCase;
import com.uniquindio.vent2bebidas.application.usecase.ObtenerBebidaUseCase;
import com.uniquindio.vent2bebidas.application.usecase.RegistrarBebidaUseCase;
import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import com.uniquindio.vent2bebidas.infrastructure.rest.mapper.BebidaMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

// Controller DELGADO: recibe, delega en el caso de uso y responde. Sin reglas de negocio.
@RestController
@RequestMapping("/api/bebidas")
public class BebidaController {
    private final BuscarBebidasUseCase buscarBebidasUseCase;
    private final ObtenerBebidaUseCase obtenerBebidaUseCase;
    private final RegistrarBebidaUseCase registrarBebidaUseCase;
    private final CambiarPrecioBebidaUseCase cambiarPrecioUseCase;
    private final EliminarBebidaUseCase eliminarBebidaUseCase;
    private final BebidaMapper mapper;
    private final UsuarioAutenticado usuarioAutenticado;

    public BebidaController(BuscarBebidasUseCase buscarBebidasUseCase,
                            ObtenerBebidaUseCase obtenerBebidaUseCase,
                            RegistrarBebidaUseCase registrarBebidaUseCase,
                            CambiarPrecioBebidaUseCase cambiarPrecioUseCase,
                            EliminarBebidaUseCase eliminarBebidaUseCase,
                            BebidaMapper mapper,
                            UsuarioAutenticado usuarioAutenticado) {
        this.buscarBebidasUseCase = buscarBebidasUseCase;
        this.obtenerBebidaUseCase = obtenerBebidaUseCase;
        this.registrarBebidaUseCase = registrarBebidaUseCase;
        this.cambiarPrecioUseCase = cambiarPrecioUseCase;
        this.eliminarBebidaUseCase = eliminarBebidaUseCase;
        this.mapper = mapper;
        this.usuarioAutenticado = usuarioAutenticado;
    }

    // GET /api/bebidas?texto=cerveza&grupo=FERMENTADA&pagina=0&tamano=10
    @GetMapping
    public PageResponse<BebidaDetalleResponse> buscar(
            @RequestParam(name = "texto", required = false) String texto,
            @RequestParam(name = "grupo", required = false) GrupoAlcoholico grupo,
            @RequestParam(name = "pagina", defaultValue = "0") int pagina,
            @RequestParam(name = "tamano", defaultValue = "10") int tamano) {
        return PageResponse.desde(buscarBebidasUseCase.ejecutar(texto, grupo, pagina, tamano),
                mapper::toResponse);
    }

    // GET /api/bebidas/{id}
    @GetMapping("/{id}")
    public BebidaDetalleResponse obtener(@PathVariable("id") UUID id) {
        return mapper.toResponse(obtenerBebidaUseCase.ejecutar(id));
    }

    // POST /api/bebidas (solo VENDEDOR). El vendedor sale del claim "uid" del token.
    @PostMapping
    public ResponseEntity<BebidaDetalleResponse> registrar(@Valid @RequestBody RegistrarBebidaRequest request) {
        Bebida bebida = registrarBebidaUseCase.ejecutar(
                usuarioAutenticado.id(),
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

    // PUT /api/bebidas/{id}/precio (solo VENDEDOR, y solo el dueño de la bebida)
    @PutMapping("/{id}/precio")
    public ResponseEntity<Void> cambiarPrecio(@PathVariable("id") UUID id,
                                              @Valid @RequestBody CambiarPrecioRequest request) {
        cambiarPrecioUseCase.ejecutar(id, usuarioAutenticado.id(), mapper.toPrecio(request));
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/bebidas/{id} (solo VENDEDOR dueño). Borrado LÓGICO: la fila no se borra.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") UUID id) {
        eliminarBebidaUseCase.ejecutar(id, usuarioAutenticado.id());
        return ResponseEntity.noContent().build();
    }
}
