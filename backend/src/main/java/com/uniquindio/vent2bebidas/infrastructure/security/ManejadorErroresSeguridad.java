package com.uniquindio.vent2bebidas.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

// Los errores de seguridad ocurren en los FILTROS, antes de llegar a cualquier controller,
// por eso el GlobalExceptionHandler no los ve. Aquí se responden con el mismo formato JSON.
//   401 Unauthorized -> no sabemos quién eres (sin token, token vencido o con firma inválida)
//   403 Forbidden    -> sabemos quién eres, pero tu rol no tiene permiso
@Component
public class ManejadorErroresSeguridad implements AuthenticationEntryPoint, AccessDeniedHandler {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException ex) throws IOException {
        response.setHeader("WWW-Authenticate", "Bearer");
        escribir(response, 401, "Unauthorized",
                "Debe iniciar sesión: falta el token o no es válido (firma incorrecta o vencido).");
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        escribir(response, 403, "Forbidden", "Su rol no tiene permiso para realizar esta operación.");
    }

    private void escribir(HttpServletResponse response, int status, String error, String mensaje) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("""
                {"timestamp":"%s","status":%d,"error":"%s","mensaje":"%s"}"""
                .formatted(LocalDateTime.now(), status, error, mensaje));
    }
}