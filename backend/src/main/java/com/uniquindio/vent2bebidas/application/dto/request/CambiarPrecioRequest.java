package com.uniquindio.vent2bebidas.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

// Mapea a: Bebida.cambiarPrecio(...)  (caso de uso CambiarPrecioBebidaUseCase)
// El id de la bebida viaja en la URL, no en el cuerpo.
public record CambiarPrecioRequest(
        @Positive double monto,                          // nuevo precio
        @NotBlank @Size(min = 3, max = 3) String moneda  // debe coincidir con la actual
) {
}