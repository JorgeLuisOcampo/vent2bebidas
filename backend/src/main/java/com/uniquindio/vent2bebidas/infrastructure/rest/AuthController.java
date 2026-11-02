package com.uniquindio.vent2bebidas.infrastructure.rest;

import com.uniquindio.vent2bebidas.application.dto.request.LoginRequest;
import com.uniquindio.vent2bebidas.application.dto.request.RegistroCompradorRequest;
import com.uniquindio.vent2bebidas.application.dto.request.RegistroVendedorRequest;
import com.uniquindio.vent2bebidas.application.dto.response.TokenResponse;
import com.uniquindio.vent2bebidas.application.dto.response.UsuarioRegistradoResponse;
import com.uniquindio.vent2bebidas.infrastructure.security.AutenticacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// Rutas PÚBLICAS: aquí se consigue el token. @SecurityRequirements vacío = Swagger no pide token.
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Registro e inicio de sesión (públicos)")
@SecurityRequirements
public class AuthController {

    private final AutenticacionService autenticacionService;

    public AuthController(AutenticacionService autenticacionService) {
        this.autenticacionService = autenticacionService;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión",
            description = "Devuelve un JWT con el correo, el rol y la fecha de vencimiento. 401 si las credenciales fallan.")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return autenticacionService.login(request.email(), request.password());
    }

    @PostMapping("/registro/comprador")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrarse como comprador",
            description = "409 si el correo ya existe; 422 si el código de consumidor ya está en uso.")
    public UsuarioRegistradoResponse registrarComprador(@Valid @RequestBody RegistroCompradorRequest request) {
        return autenticacionService.registrarComprador(request);
    }

    @PostMapping("/registro/vendedor")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrarse como vendedor", description = "409 si el correo ya existe.")
    public UsuarioRegistradoResponse registrarVendedor(@Valid @RequestBody RegistroVendedorRequest request) {
        return autenticacionService.registrarVendedor(request);
    }
}