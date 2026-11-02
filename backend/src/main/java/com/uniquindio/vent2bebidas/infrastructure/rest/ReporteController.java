package com.uniquindio.vent2bebidas.infrastructure.rest;

import com.uniquindio.vent2bebidas.application.dto.response.BebidaVendidaResponse;
import com.uniquindio.vent2bebidas.application.usecase.ConsultarBebidasMasVendidasUseCase;
import com.uniquindio.vent2bebidas.domain.repository.BebidaVendida;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "Reportes calculados por la base de datos (VENDEDOR)")
public class ReporteController {

    private final ConsultarBebidasMasVendidasUseCase masVendidasUseCase;

    public ReporteController(ConsultarBebidasMasVendidasUseCase masVendidasUseCase) {
        this.masVendidasUseCase = masVendidasUseCase;
    }

    // GET /api/reportes/bebidas-mas-vendidas?limite=5  (solo VENDEDOR)
    @GetMapping("/bebidas-mas-vendidas")
    @Operation(summary = "Ranking de bebidas más vendidas (VENDEDOR)",
            description = "Suma las unidades de las compras CONFIRMADAS por bebida (JOIN + GROUP BY + SUM + COUNT). "
                    + "No incluye compras canceladas ni bebidas eliminadas.")
    public List<BebidaVendidaResponse> masVendidas(
            @RequestParam(name = "limite", defaultValue = "5") int limite) {
        List<BebidaVendida> ranking = masVendidasUseCase.ejecutar(limite);
        return IntStream.range(0, ranking.size())
                .mapToObj(i -> BebidaVendidaResponse.desde(i + 1, ranking.get(i)))
                .toList();
    }
}