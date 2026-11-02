package com.uniquindio.vent2bebidas.infrastructure.security;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

// Arma el token: HEADER.PAYLOAD.FIRMA
//  - header : algoritmo de firma (HS256)
//  - payload: correo, uid, roles, emisión y vencimiento (se puede LEER, por eso nunca lleva la contraseña)
//  - firma  : HMAC del header y el payload con la clave secreta (si alguien cambia el payload, la firma no coincide)
@Component
public class JwtTokenProvider {

    private static final String EMISOR = "vent2bebidas-api";
    private final JwtEncoder encoder;

    public JwtTokenProvider(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public String generar(String email, String usuarioId, List<String> roles, Instant emitidoEn, Instant expiraEn) {
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(EMISOR)
                .issuedAt(emitidoEn)
                .expiresAt(expiraEn)      // claim "exp": fecha de vencimiento
                .subject(email)           // claim "sub": el correo
                .claim("email", email)
                .claim("uid", usuarioId)  // id del consumidor o del vendedor
                .claim("roles", roles)    // ["COMPRADOR"] o ["VENDEDOR"]
                .build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
