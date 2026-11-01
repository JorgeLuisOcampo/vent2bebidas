package com.uniquindio.vent2bebidas.infrastructure.rest;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

// Entrega el id del usuario que hace la petición, leído del claim "uid" del token JWT.
// Así el comprador o el vendedor NUNCA viajan en el cuerpo de la petición:
// nadie puede comprar o publicar a nombre de otro.
//
// Es un @Component que se inyecta en los controllers. En las pruebas @WebMvcTest
// (que no cargan la seguridad) se reemplaza con @MockitoBean y se le dice qué id devolver.
@Component
public class UsuarioAutenticado {

    public UUID id() {
        // Spring Security deja aquí el token después de verificar su firma y su vencimiento.
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (autenticacion != null && autenticacion.getPrincipal() instanceof Jwt token) ? token : null;
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
