package com.uniquindio.vent2bebidas.infrastructure.security;

import com.uniquindio.vent2bebidas.application.dto.request.RegistroCompradorRequest;
import com.uniquindio.vent2bebidas.application.dto.request.RegistroVendedorRequest;
import com.uniquindio.vent2bebidas.application.dto.response.TokenResponse;
import com.uniquindio.vent2bebidas.application.dto.response.UsuarioRegistradoResponse;
import com.uniquindio.vent2bebidas.application.usecase.RegistrarConsumidorUseCase;
import com.uniquindio.vent2bebidas.domain.entity.Consumidor;
import com.uniquindio.vent2bebidas.domain.valueobject.CodigoConsumidor;
import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.UsuarioJpaDataRepository;
import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity.RolUsuario;
import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity.UsuarioEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

// Servicio de SEGURIDAD (infraestructura): login y registro de usuarios.
// Las reglas del consumidor (código único, fecha válida) las sigue aplicando el dominio
// a través de RegistrarConsumidorUseCase.
@Service
public class AutenticacionService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UsuarioJpaDataRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegistrarConsumidorUseCase registrarConsumidorUseCase;
    private final long minutosDeVida;

    public AutenticacionService(AuthenticationManager authenticationManager,
                                JwtTokenProvider jwtTokenProvider,
                                UsuarioJpaDataRepository usuarioRepository,
                                PasswordEncoder passwordEncoder,
                                RegistrarConsumidorUseCase registrarConsumidorUseCase,
                                @Value("${app.jwt.expiracion-minutos}") long minutosDeVida) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.registrarConsumidorUseCase = registrarConsumidorUseCase;
        this.minutosDeVida = minutosDeVida;
    }

    // Si el correo o la contraseña no coinciden, AuthenticationManager lanza
    // BadCredentialsException y el GlobalExceptionHandler responde 401.
    public TokenResponse login(String email, String password) {
        Authentication autenticacion = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizar(email), password));
        UsuarioEntity usuario = ((CustomUserDetails) autenticacion.getPrincipal()).usuario();

        List<String> roles = List.of(usuario.getRol().name());
        Instant ahora = Instant.now();
        Instant expira = ahora.plus(minutosDeVida, ChronoUnit.MINUTES);
        String token = jwtTokenProvider.generar(usuario.getEmail(), usuario.getId(), roles, ahora, expira);
        return new TokenResponse(token, "Bearer", expira, usuario.getEmail(), roles);
    }

    // Crea el Consumidor (dominio) y su usuario (seguridad) con el MISMO id, en una sola transacción.
    @Transactional
    public UsuarioRegistradoResponse registrarComprador(RegistroCompradorRequest request) {
        String email = normalizar(request.email());
        verificarCorreoDisponible(email);
        Consumidor consumidor = registrarConsumidorUseCase.ejecutar(
                request.nombre(), new CodigoConsumidor(request.codigo()), request.fechaNacimiento());
        guardarUsuario(consumidor.getId(), email, request.password(), consumidor.getNombre(), RolUsuario.COMPRADOR);
        return new UsuarioRegistradoResponse(consumidor.getId(), email, consumidor.getNombre(),
                RolUsuario.COMPRADOR.name());
    }

    @Transactional
    public UsuarioRegistradoResponse registrarVendedor(RegistroVendedorRequest request) {
        String email = normalizar(request.email());
        verificarCorreoDisponible(email);
        UUID vendedorId = UUID.randomUUID();
        guardarUsuario(vendedorId, email, request.password(), request.nombre().trim(), RolUsuario.VENDEDOR);
        return new UsuarioRegistradoResponse(vendedorId, email, request.nombre().trim(), RolUsuario.VENDEDOR.name());
    }

    private void verificarCorreoDisponible(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new CorreoYaRegistradoException(email);
        }
    }

    private void guardarUsuario(UUID id, String email, String password, String nombre, RolUsuario rol) {
        // La contraseña se guarda como hash BCrypt, nunca en texto plano.
        usuarioRepository.save(new UsuarioEntity(id.toString(), email, passwordEncoder.encode(password), nombre, rol));
    }

    private String normalizar(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}