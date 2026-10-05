package com.uniquindio.vent2bebidas.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

// Mapea a: Consumidor.registrar(...)  (caso de uso RegistrarConsumidorUseCase)
public record RegistrarConsumidorRequest(
        @NotBlank String nombre,                         // nombre del consumidor
        @NotBlank @Size(min = 6, max = 6) String codigo, // código de acceso al catálogo
        @NotNull @Past LocalDate fechaNacimiento         // para verificar mayoría de edad
) {
}