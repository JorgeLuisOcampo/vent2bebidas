# Guía de la demo en Swagger (Bloques 6 y 7 del video)

Arrancar con `./gradlew bootRun` (desde `backend`) y abrir http://localhost:8080/swagger-ui.html.
Los ids de bebidas y envases son los de `data.sql`.

## Bloque 6 · Seguridad

1. **Login en vivo** — `POST /api/auth/login`
   ```json
   { "email": "cerveceria@vent2bebidas.com", "password": "Vendedor123*" }
   ```
   Copiar el `token` y pegarlo en https://jwt.io:
    - *Header*: `{"alg":"HS256"}` → con qué fórmula se firma.
    - *Payload*: `sub`/`email` (correo), `uid`, `roles`, `iat` (emisión) y `exp` (vencimiento). Se puede leer porque solo está en Base64; por eso **nunca** lleva la contraseña.
    - *Firma*: HMAC-SHA256 de header + payload con `app.jwt.secret`. Si alguien cambia el payload, la firma deja de coincidir y la API responde 401.
2. **Sin token → 401**: sin pulsar Authorize, ejecutar `GET /api/reportes/bebidas-mas-vendidas`.
3. **Rol equivocado → 403**: hacer login con `ana@vent2bebidas.com` / `Comprador123*`, autorizar con ese token y repetir `GET /api/reportes/bebidas-mas-vendidas`.
4. **Rol correcto → 200**: autorizar con el token del vendedor y repetir.
5. Mostrar `SecurityConfig.java` (reglas por rol), `AutenticacionConfig.java` (BCrypt) y la tabla `USUARIOS` en la consola H2 (las contraseñas son hashes `$2a$10$...`).

## Bloque 7 · Flujo de punta a punta

| # | Quién (token) | Petición | Resultado esperado |
|---|---|---|---|
| 1 | Vendedor `cerveceria@...` | `POST /api/bebidas` con el cuerpo de abajo | 201 + `Location` |
| 2 | Compradora `ana@...` | `GET /api/bebidas?texto=ipa` | La bebida nueva aparece en la página |
| 3 | Compradora `ana@...` | `POST /api/compras` `{ "bebidaId": "<id del paso 1>", "unidades": 2 }` | 201, `total` = 2 × precio |
| 4 | Compradora `ana@...` | `GET /api/compras/mias` | La compra aparece primero |
| 5 | Compradora `ana@...` | `PUT /api/compras/{id}/cancelar` | 200, estado `CANCELADA` |
| 6 | Compradora `ana@...` | repetir el paso 5 | **422** "La compra ya está cancelada." |
| 7 | Compradora `sofia@...` (menor) | `POST /api/compras` `{ "bebidaId": "b0000000-0000-0000-0000-000000000001", "unidades": 1 }` | **422** "Un menor de edad no puede comprar bebidas alcohólicas." |
| 8 | Vendedor `licores@...` | `PUT /api/bebidas/b0000000-0000-0000-0000-000000000001/precio` `{ "monto": 9000, "moneda": "COP" }` | **422** "Solo el vendedor que publicó la bebida puede modificarla." |
| 9 | Vendedor `cerveceria@...` | `GET /api/reportes/bebidas-mas-vendidas?limite=5` | Cerveza Rubia Artesanal primera con 13 unidades |
| 10 | Vendedor `cerveceria@...` | `PUT /api/envases/e0000000-0000-0000-0000-000000000002/retorno` | **422** "El envase alcanzó su límite de usos..." |

Cuerpo del paso 1:
```json
{
  "nombre": "Cerveza IPA del Quindio",
  "material": "VIDRIO",
  "capacidadMl": 330,
  "retornable": true,
  "grupoAlcoholico": "FERMENTADA",
  "monto": 7000,
  "moneda": "COP"
}
```

Para el **error de negocio a propósito** explicar el camino de la excepción: `Compra.cancelar(...)` lanza
`ReglaDominioException` → el caso de uso no la atrapa → `GlobalExceptionHandler.manejarReglaDominio` la
convierte en **422** con el mensaje en JSON.

## Consultas en la consola H2

http://localhost:8080/h2-console — JDBC URL `jdbc:h2:mem:vent2bebidas`, usuario `sa`, sin contraseña.

```sql
SELECT * FROM BEBIDAS;
SELECT * FROM COMPRAS ORDER BY FECHA DESC;
SELECT * FROM USUARIOS;

-- La misma consulta del ranking, en SQL:
SELECT b.nombre, SUM(c.unidades) AS unidades, COUNT(*) AS compras
FROM compras c JOIN bebidas b ON b.id = c.bebida_id
WHERE c.estado = 'CONFIRMADA' AND b.estado = 'PUBLICADA'
GROUP BY b.id, b.nombre
ORDER BY unidades DESC;
```