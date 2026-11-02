# Vent2Bebidas

Marketplace de bebidas con **envases retornables**. Los vendedores publican sus
bebidas y los consumidores las compran; el sistema controla además el ciclo de
vida de cada envase (cuántas veces se ha reutilizado, si está dañado o descartado).

Proyecto de **Programación Avanzada 2026-2** — Universidad del Quindío.
**Entrega 2: backend completo** (API REST + persistencia JPA + seguridad JWT).

## Integrantes

| Integrante | Parte principal en la Entrega 2 |
|---|---|
| Juan David Torres Arango | Dominio (agregado `Compra`, reglas RN-11 a RN-13), casos de uso del comprador y API de compras |
| Jorge Luis Ocampo Ocampo | Persistencia JPA (Consumidor, Envase, Compra), `data.sql`, ranking con JOIN/GROUP BY, API de envases y pruebas de integración |
| Oscar Leandro Agudelo Franco | Seguridad JWT (registro, login, BCrypt, roles), Swagger, manejo de errores HTTP y README |

## Correcciones de la Entrega 1

| Observación que nos dieron | Qué cambiamos | Dónde se ve (archivo o rama) |
|---|---|---|
| Las reglas de negocio rotas respondían 400, igual que un dato mal escrito | Ahora una regla rota responde **422** y los datos inválidos **400** con el detalle de cada campo | `GlobalExceptionHandler.java`, rama `fix/codigos-http-errores` |
| La regla del código de consumidor único estaba en el caso de uso (un `if` de negocio) | La movimos a un **servicio de dominio**; el caso de uso solo lo llama | `VerificadorCodigoConsumidor.java`, `RegistrarConsumidorUseCase.java`, rama `fix/regla-unicidad-codigo-dominio` |
| Cualquier vendedor podía cambiar el precio o eliminar la bebida de otro | La raíz `Bebida` verifica que quien la modifica sea su dueño (RN-11); el vendedor sale del token | `Bebida.java` (`verificarQuePerteneceA`), rama `fix/propiedad-bebida-vendedor` |
| Validar la página negativa lanzaba una excepción de dominio | Es validación de entrada: ahora es `IllegalArgumentException` (400) | `BuscarBebidasUseCase.java`, rama `fix/codigos-http-errores` |
| `Consumidor` y `Envase` solo se guardaban en memoria | Repositorios JPA reales con entidades y mappers separados; los de memoria quedan para las pruebas | `ConsumidorRepositoryJpa.java`, `EnvaseRepositoryJpa.java`, ramas `feature/persistencia-consumidor` y `feature/persistencia-envase` |
| Confirmar una compra solo sumaba un contador y no quedaba registro de la venta | Nuevo agregado `Compra` con sus invariantes; permite el historial y el ranking | `Compra.java`, `ConfirmarCompraDeBebidaUseCase.java`, ramas `feature/dominio-compra` y `feature/casos-uso-compra` |

## Cómo ejecutar

Requisitos: **Java 21**. No hace falta instalar Gradle ni una base de datos (se usa H2 en memoria).

```bash
cd backend
./gradlew bootRun          # Windows: gradlew.bat bootRun
```

| Qué | Dónde |
|---|---|
| Swagger (documentación y pruebas) | http://localhost:8080/swagger-ui.html |
| Consola de la base H2 | http://localhost:8080/h2-console — JDBC URL `jdbc:h2:mem:vent2bebidas`, usuario `sa`, sin contraseña |
| Pruebas automáticas | `./gradlew test` (desde `backend`) |

### Probar en Swagger

1. Abrir `POST /api/auth/login` y enviar uno de los usuarios de prueba.
2. Copiar el valor de `token` de la respuesta.
3. Pulsar **Authorize** (arriba a la derecha), pegar el token y aceptar.
4. Ya se pueden llamar los endpoints del rol de ese usuario.

## Usuarios de prueba

Se crean al arrancar (`UsuariosIniciales`) con la contraseña guardada en **BCrypt**.
Sus ids coinciden con los vendedores y consumidores de `data.sql`.

| Correo | Contraseña | Rol | Para qué sirve en la demo |
|---|---|---|---|
| cerveceria@vent2bebidas.com | Vendedor123* | VENDEDOR | Dueño de la Cerveza Rubia, Negra, Malta y Jugo |
| licores@vent2bebidas.com | Vendedor123* | VENDEDOR | Dueño del Aguardiente y el Ron (no puede tocar las bebidas del otro) |
| ana@vent2bebidas.com | Comprador123* | COMPRADOR | Mayor de edad con 5 compras: puede comprar más de 5 unidades |
| carlos@vent2bebidas.com | Comprador123* | COMPRADOR | Mayor de edad con 3 compras: si pide más de 5 unidades rompe RN-09 |
| sofia@vent2bebidas.com | Comprador123* | COMPRADOR | **Menor de edad**: si compra alcohol rompe RN-08 |

## Endpoints principales

| Método | Ruta | Rol | Caso de uso |
|---|---|---|---|
| POST | `/api/auth/registro/comprador` | público | Registrar comprador (crea el Consumidor) |
| POST | `/api/auth/registro/vendedor` | público | Registrar vendedor |
| POST | `/api/auth/login` | público | Iniciar sesión (devuelve el JWT) |
| GET | `/api/bebidas?texto=&grupo=&pagina=&tamano=` | COMPRADOR, VENDEDOR | Buscar bebidas (paginado, con filtro) |
| GET | `/api/bebidas/{id}` | COMPRADOR, VENDEDOR | Consultar bebida |
| POST | `/api/bebidas` | VENDEDOR | Publicar bebida |
| PUT | `/api/bebidas/{id}/precio` | VENDEDOR (dueño) | Cambiar precio |
| DELETE | `/api/bebidas/{id}` | VENDEDOR (dueño) | Retirar bebida (borrado lógico) |
| POST | `/api/compras` | COMPRADOR | Comprar bebida |
| GET | `/api/compras/mias` | COMPRADOR | Listar mis compras |
| PUT | `/api/compras/{id}/cancelar` | COMPRADOR (dueño) | Cancelar compra (máx. 24 h) |
| GET | `/api/consumidores/yo` | COMPRADOR | Ver mi perfil |
| POST | `/api/envases` | VENDEDOR | Registrar envase |
| GET | `/api/envases/{id}` | VENDEDOR | Consultar envase |
| PUT | `/api/envases/{id}/retorno` | VENDEDOR | Registrar retorno del envase |
| PUT | `/api/envases/{id}/danado` | VENDEDOR | Marcar envase como dañado |
| PUT | `/api/envases/{id}/descarte` | VENDEDOR | Descartar envase (borrado lógico) |
| GET | `/api/reportes/bebidas-mas-vendidas?limite=5` | VENDEDOR | Ranking de bebidas más vendidas |

### Códigos de respuesta

| Código | Cuándo |
|---|---|
| 400 | Datos inválidos (validaciones del DTO, JSON mal formado, parámetro con tipo incorrecto) |
| 401 | Sin token, token alterado/vencido, o login con credenciales incorrectas |
| 403 | Token válido pero sin el rol necesario |
| 404 | El recurso no existe |
| 409 | El correo ya está registrado |
| 422 | Se rompe una regla de negocio del dominio (`ReglaDominioException`) |

Todos los errores responden JSON con `timestamp`, `status`, `error` y `mensaje`.

## Arquitectura

Arquitectura hexagonal con tres capas dentro de `backend/`:

| Capa | Responsabilidad |
|---|---|
| `domain` | Entidades, Value Objects, reglas de negocio, servicio de dominio e interfaces de Repository (puertos). **Sin imports de Spring ni JPA.** |
| `application` | Casos de uso (orquestan el dominio) y DTOs de entrada y salida. |
| `infrastructure` | Adaptadores: REST (controllers, mappers, errores), persistencia JPA (entidades, mappers, repositorios), seguridad JWT y configuración. |

## Modelo del dominio

Cuatro agregados, cada uno con su propia raíz (constructor privado y método de fábrica):

- **Envase**: ciclo de vida de un envase retornable (nuevo, retornado, dañado, descartado).
- **Bebida**: producto del vendedor, con precio, dueño y borrado lógico.
- **Consumidor**: comprador, con reglas de edad y de volumen de compra.
- **Compra**: registra qué consumidor compró cuántas unidades de qué bebida, a qué precio y cuándo.

Value Objects: `Capacidad`, `MaterialEnvase`, `TipoEnvase`, `EstadoEnvase`, `GrupoAlcoholico`,
`Precio`, `CodigoConsumidor`, `EstadoBebida`, `EstadoCompra`.
Servicio de dominio: `VerificadorCodigoConsumidor`. Excepción de dominio: `ReglaDominioException`.

## Reglas de negocio destacadas

- Un envase de aluminio nunca puede ser retornable.
- Un envase no puede superar el máximo de usos de su material (vidrio 30, plástico 10).
- Un envase dañado solo puede descartarse; uno descartado ya no cambia.
- Un menor de edad no puede comprar bebidas alcohólicas.
- Comprar más de 5 unidades de una vez exige más de 3 compras históricas.
- Una bebida eliminada no puede modificarse ni venderse (borrado lógico).
- Solo el vendedor que publicó una bebida puede cambiarle el precio o eliminarla.
- Una compra lleva entre 1 y 24 unidades.
- Solo quien hizo la compra puede cancelarla, y solo dentro de las 24 horas siguientes.

El detalle (por qué existe, dónde se protege y qué prueba lo demuestra) está en `docs/reglas-de-negocio.md`.

## Persistencia y consultas

- H2 en memoria; tablas creadas por Hibernate y datos de prueba en `backend/src/main/resources/data.sql`.
- Entidades JPA separadas del dominio (`infrastructure/persistence/jpa/entity`) con mappers.
- Borrado lógico: las bebidas pasan a `ELIMINADA` y los envases a `DESCARTADO`; nunca se borra la fila.
- **Listado paginado**: `BebidaJpaDataRepository.buscar` (JPQL con parámetros con nombre, texto + grupo alcohólico).
- **Ranking**: `CompraJpaDataRepository.masVendidas` (JPQL con `JOIN`, `GROUP BY`, `SUM` y `COUNT`).

## Pruebas

`./gradlew test` ejecuta más de 150 pruebas, entre ellas:

| Tipo | Ejemplos |
|---|---|
| Dominio | `BebidaTest`, `ConsumidorTest`, `EnvaseTest`, `CompraTest`, `PrecioTest`, `TipoEnvaseTest` |
| Casos de uso (repositorio en memoria o Mockito) | `ConfirmarCompraDeBebidaUseCaseTest`, `CancelarCompraUseCaseTest`, `CambiarPrecioBebidaUseCaseTest` |
| Controller (`@WebMvcTest`) | `CompraControllerTest`, `BebidaControllerErroresTest` |
| Repositorio (`@DataJpaTest`) | `CompraRepositoryDataJpaTest`, `BebidaRepositoryDataJpaTest` |
| Seguridad | `SeguridadRolesEndpointTest`, `SeguridadBebidaEndpointTest`, `AutenticacionIntegracionTest` |
| Integración (sin mocks) | `FlujoCompraIntegracionTest`, `BebidaIntegracionTest` |

## Documentación

- `glosario-lenguaje-ubicuo.md`: términos del negocio y su uso en el código.
- `docs/`: diagramas, reglas de negocio, casos de uso y guía de la demo.

## Convenciones de trabajo

- Mensajes de commit con prefijo: `feat`, `fix`, `refactor`, `test`, `docs`, `build`.
- Una rama por funcionalidad; todo cambio entra a `main` por Pull Request.

## Pruebas avanzadas
Todas las pruebas corren con `./gradlew test` (desde la carpeta `backend`).

### 1. Probar un endpoint con seguridad
Probamos rutas protegidas con tres casos: sin token responde 401 (el sistema no sabe quién eres),
con el rol equivocado responde 403 (sabe quién eres pero no tienes permiso) y con el rol correcto
responde 200 (`SeguridadRolesEndpointTest`, `SeguridadBebidaEndpointTest`). Usamos `@SpringBootTest`
con `@AutoConfigureMockMvc` para que se cargue nuestra seguridad real, y `jwt()` de
`spring-security-test` para simular un usuario con el rol que queramos sin pasar por el login.
Además, `AutenticacionIntegracionTest` hace el login real y usa el token firmado de verdad.
Un `@WebMvcTest` no carga nuestro `SecurityConfig`, por eso ahí apagamos los filtros con `addFilters = false`.

Lo que más nos costó: [escribe aquí tu experiencia].

### 2. @DataJpaTest contra @SpringBootTest
`@SpringBootTest` levanta toda la aplicación; `@DataJpaTest` levanta solo la capa de
persistencia (entidades y repositorios de Spring Data), así que es más rápida y
deshace cada prueba sola. Como no carga nuestros `@Repository` ni `@Component`, tuvimos que
traer el adaptador y el mapper con `@Import`. En Spring Boot 4 el paquete de `@DataJpaTest`
cambió a `org.springframework.boot.data.jpa.test.autoconfigure` y necesita el starter
de pruebas `spring-boot-starter-data-jpa-test`.

Lo que más nos costó: [escribe aquí tu experiencia].

### 3. Prueba de integración
Una prueba de integración recorre varias piezas funcionando juntas de verdad: la petición entra
por el controller, pasa por el caso de uso, el dominio, el repositorio JPA y llega a la base H2,
sin ningún `@MockitoBean`. `FlujoCompraIntegracionTest` inicia sesión de verdad, publica una
bebida, la compra, la cancela, rompe reglas de negocio a propósito y consulta el ranking.

Lo que más nos costó: [escribe aquí tu experiencia].

### 4. Más casos en el controller
`CompraControllerTest` y `BebidaControllerErroresTest` prueban el 404 (recurso que no existe),
el 422 (regla de negocio rota) y, con `verify(..., never())`, que el caso de uso o el guardado
nunca se llaman cuando los datos son inválidos o algo falla (`CancelarCompraUseCaseTest`,
`CambiarPrecioBebidaUseCaseTest`).