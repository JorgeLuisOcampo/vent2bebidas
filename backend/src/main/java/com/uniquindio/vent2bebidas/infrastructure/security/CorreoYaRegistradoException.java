package com.uniquindio.vent2bebidas.infrastructure.security;

// El correo es un dato de SEGURIDAD (credenciales), no del dominio: por eso esta excepción
// vive en infraestructura. El GlobalExceptionHandler la convierte en 409 Conflict.
public class CorreoYaRegistradoException extends RuntimeException {

    public CorreoYaRegistradoException(String email) {
        super("Ya existe un usuario registrado con el correo " + email + ".");
    }
}