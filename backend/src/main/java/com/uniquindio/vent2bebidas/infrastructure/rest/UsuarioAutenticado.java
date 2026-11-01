package com.uniquindio.vent2bebidas.infrastructure.rest;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

// Lee del token JWT el id del usuario que hace la petición (claim "uid").
// Así el comprador o el vendedor NUNCA viajan en el cuerpo de la petición:
// nadie puede comprar o publicar a nombre de otro.
public final class UsuarioAutenticado {

    private UsuarioAutenticado() {
    }

    public static UUID idDe(Jwt jwt) {
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
