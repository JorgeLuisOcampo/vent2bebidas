package com.uniquindio.vent2bebidas.infrastructure.rest;

import com.uniquindio.vent2bebidas.application.dto.request.ConfirmarCompraRequest;
import com.uniquindio.vent2bebidas.application.dto.response.CompraResponse;
import com.uniquindio.vent2bebidas.application.usecase.CancelarCompraUseCase;
import com.uniquindio.vent2bebidas.application.usecase.ConfirmarCompraDeBebidaUseCase;
import com.uniquindio.vent2bebidas.application.usecase.ListarComprasDelConsumidorUseCase;
import com.uniquindio.vent2bebidas.domain.entity.Compra;
import com.uniquindio.vent2bebidas.infrastructure.rest.mapper.CompraMapper;
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
import java.util.List;
import java.util.UUID;

// Controller DELGADO de compras (rol COMPRADOR): recibe, delega en el caso de uso y responde.
@RestController
@RequestMapping("/api/compras")
public class CompraController {

    private final ConfirmarCompraDeBebidaUseCase confirmarCompraUseCase;
    private final CancelarCompraUseCase cancelarCompraUseCase;
    private final ListarComprasDelConsumidorUseCase listarComprasUseCase;
    private final CompraMapper mapper;
    private final UsuarioAutenticado usuarioAutenticado;

    public CompraController(ConfirmarCompraDeBebidaUseCase confirmarCompraUseCase,
                            CancelarCompraUseCase cancelarCompraUseCase,
                            ListarComprasDelConsumidorUseCase listarComprasUseCase,
                            CompraMapper mapper,
                            UsuarioAutenticado usuarioAutenticado) {
        this.confirmarCompraUseCase = confirmarCompraUseCase;
        this.cancelarCompraUseCase = cancelarCompraUseCase;
        this.listarComprasUseCase = listarComprasUseCase;
        this.mapper = mapper;
        this.usuarioAutenticado = usuarioAutenticado;
    }

    // POST /api/compras  (solo COMPRADOR). El consumidor sale del token.
    @PostMapping
    public ResponseEntity<CompraResponse> comprar(@Valid @RequestBody ConfirmarCompraRequest request) {
        Compra compra = confirmarCompraUseCase.ejecutar(
                usuarioAutenticado.id(), request.bebidaId(), request.unidades());
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/compras/{id}")
                .buildAndExpand(compra.getId())
                .toUri();
        return ResponseEntity.created(location).body(mapper.toResponse(compra));
    }

    // GET /api/compras/mias  (solo COMPRADOR)
    @GetMapping("/mias")
    public List<CompraResponse> misCompras() {
        return listarComprasUseCase.ejecutar(usuarioAutenticado.id()).stream()
                .map(mapper::toResponse)
                .toList();
    }

    // PUT /api/compras/{id}/cancelar  (solo COMPRADOR, y solo el dueño de la compra)
    @PutMapping("/{id}/cancelar")
    public CompraResponse cancelar(@PathVariable("id") UUID id) {
        return mapper.toResponse(cancelarCompraUseCase.ejecutar(id, usuarioAutenticado.id()));
    }
}
