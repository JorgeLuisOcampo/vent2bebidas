package com.uniquindio.vent2bebidas.application.dto.response;

import java.time.Instant;
import java.util.List;

// Respuesta del login. "tipo" = Bearer (portador): quien porte el token, accede.
public record TokenResponse(String token, String tipo, Instant expiraEn, String email, List<String> roles) {
}