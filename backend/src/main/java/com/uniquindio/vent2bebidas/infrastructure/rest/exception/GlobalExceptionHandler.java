package com.uniquindio.vent2bebidas.infrastructure.rest.exception;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

// Intercepta las excepciones de CUALQUIER controller y las convierte en un código HTTP
// con un cuerpo JSON uniforme: { timestamp, status, error, mensaje, [campos] }.
//
//   400 -> los datos que llegaron son inválidos (validaciones del DTO, JSON mal formado, tipos)
//   404 -> el recurso no existe
//   422 -> los datos son válidos, pero rompen una REGLA DE NEGOCIO del dominio
//   401/403 -> los responde la configuración de seguridad
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 422 Unprocessable Content: la petición se entendió, pero el negocio no la permite.
    private static final HttpStatus REGLA_DE_NEGOCIO_ROTA = HttpStatus.valueOf(422);

    // Regla de negocio rota: la lanza el propio objeto del dominio.
    @ExceptionHandler(ReglaDominioException.class)
    public ResponseEntity<Map<String, Object>> manejarReglaDominio(ReglaDominioException ex) {
        return construirRespuesta(REGLA_DE_NEGOCIO_ROTA, ex.getMessage()); // -> 422
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> manejarNoEncontrado(NoSuchElementException ex) {
        return construirRespuesta(HttpStatus.NOT_FOUND, "El recurso solicitado no existe."); // -> 404
    }

    // @Valid falló: se devuelve qué campo falló y por qué.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                campos.putIfAbsent(error.getField(), error.getDefaultMessage()));
        Map<String, Object> cuerpo = cuerpoBase(HttpStatus.BAD_REQUEST,
                "Los datos enviados no son válidos.");
        cuerpo.put("campos", campos);
        return ResponseEntity.badRequest().body(cuerpo); // -> 400
    }

    // JSON mal formado o un enum con un valor que no existe (ej. "material": "MADERA").
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> manejarCuerpoIlegible(HttpMessageNotReadableException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST,
                "El cuerpo de la petición no se pudo leer: revise el formato JSON y los valores."); // -> 400
    }

    // Un parámetro de la URL con el tipo equivocado (ej. un id que no es UUID).
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> manejarTipoInvalido(MethodArgumentTypeMismatchException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST,
                "El parámetro '" + ex.getName() + "' tiene un valor inválido."); // -> 400
    }

    // Validaciones de ENTRADA hechas en los casos de uso (ej. página negativa).
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarArgumentoInvalido(IllegalArgumentException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage()); // -> 400
    }

    // Errores que el controller lanza con un código explícito (ej. token sin el claim "uid").
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> manejarResponseStatus(ResponseStatusException ex) {
        HttpStatusCode codigo = ex.getStatusCode();
        HttpStatus status = HttpStatus.resolve(codigo.value());
        Map<String, Object> cuerpo = cuerpoBase(
                status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status, ex.getReason());
        return ResponseEntity.status(codigo).body(cuerpo);
    }

    private ResponseEntity<Map<String, Object>> construirRespuesta(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(cuerpoBase(status, mensaje));
    }

    private Map<String, Object> cuerpoBase(HttpStatus status, String mensaje) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now().toString());
        cuerpo.put("status", status.value());
        cuerpo.put("error", status.getReasonPhrase());
        cuerpo.put("mensaje", mensaje);
        return cuerpo;
    }
}