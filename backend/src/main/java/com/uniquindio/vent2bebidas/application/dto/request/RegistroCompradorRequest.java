package com.uniquindio.vent2bebidas.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

// Registro de un COMPRADOR: crea las credenciales y el Consumidor del dominio con el mismo id.
public record RegistroCompradorRequest(
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El código es obligatorio")
        @Size(min = 6, max = 6, message = "El código debe tener exactamente 6 caracteres")
        String codigo,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe estar en el pasado")
        LocalDate fechaNacimiento
) {
}