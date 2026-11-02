package com.uniquindio.vent2bebidas.application.dto.response;

import java.util.UUID;

// Respuesta del registro. NUNCA se devuelve la contraseña (ni su hash).
public record UsuarioRegistradoResponse(UUID id, String email, String nombre, String rol) {
}
