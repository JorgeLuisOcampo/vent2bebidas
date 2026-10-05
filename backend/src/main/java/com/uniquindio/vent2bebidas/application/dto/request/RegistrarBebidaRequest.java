package com.uniquindio.vent2bebidas.application.dto.request;

import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import com.uniquindio.vent2bebidas.domain.valueobject.MaterialEnvase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.UUID;

// Mapea a: Bebida.publicar(...)  (caso de uso RegistrarBebidaUseCase)
public record RegistrarBebidaRequest(
        @NotNull UUID vendedorId,                        // quién publica la bebida
        @NotBlank String nombre,                         // nombre que verá el comprador
        @NotNull MaterialEnvase material,                // material del envase
        @Positive int capacidadMl,                       // capacidad en mililitros
        boolean retornable,                              // si el envase se devuelve
        @NotNull GrupoAlcoholico grupoAlcoholico,        // define si exige mayoría de edad
        @Positive double monto,                          // precio de venta
        @NotBlank @Size(min = 3, max = 3) String moneda  // código de moneda, ej. COP
) {
}