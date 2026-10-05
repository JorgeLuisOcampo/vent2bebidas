# Mapeo Dominio - API y diseño de DTOs

**Proyecto:** Vent2Bebidas 

Este documento define **cómo se expone el dominio hacia afuera**: qué operación del dominio corresponde a cada endpoint y qué datos viajan en cada petición y respuesta (DTOs). Los controladores REST se programan en una entrega posterior; aquí queda el diseño.

**Convenciones:**

- Todas las rutas llevan el prefijo `/api`.
- Los cambios de estado usan `PUT` sobre un subrecurso (`/precio`, `/retorno`, `/danado`, `/descarte`).
- Los ids viajan en la URL, no en el cuerpo, cuando identifican al recurso sobre el que se actúa.

---

## 1. Mapeo operación del dominio - endpoint

### Bebida

| Operación del dominio | Caso de uso | Método HTTP | Endpoint | Request | Response |
|---|---|---|---|---|---|
| `Bebida.publicar(...)` | `RegistrarBebidaUseCase` | POST | `/api/bebidas` | `RegistrarBebidaRequest` | `201` + `BebidaDetalleResponse` |
| consultar el catálogo | `ListarBebidasDisponiblesUseCase` | GET | `/api/bebidas` | — | `200` + lista de `BebidaDetalleResponse` |
| `bebida.cambiarPrecio(...)` | `CambiarPrecioBebidaUseCase` | PUT | `/api/bebidas/{id}/precio` | `CambiarPrecioRequest` | `204` |
| `bebida.eliminarLogicamente()` | `EliminarBebidaUseCase` | DELETE | `/api/bebidas/{id}` | — | `204` |

### Envase

| Operación del dominio | Caso de uso | Método HTTP | Endpoint | Request | Response |
|---|---|---|---|---|---|
| `Envase.registrar(...)` | `RegistrarEnvaseUseCase` | POST | `/api/envases` | `RegistrarEnvaseRequest` | `201` + `EnvaseDetalleResponse` |
| `envase.registrarRetorno()` | `RegistrarRetornoEnvaseUseCase` | PUT | `/api/envases/{id}/retorno` | — | `204` |
| `envase.marcarComoDanado()` | `MarcarEnvaseComoDanadoUseCase` | PUT | `/api/envases/{id}/danado` | — | `204` |
| `envase.descartar()` | `DescartarEnvaseUseCase` | PUT | `/api/envases/{id}/descarte` | — | `204` |

### Consumidor y compra

| Operación del dominio | Caso de uso | Método HTTP | Endpoint | Request | Response |
|---|---|---|---|---|---|
| `Consumidor.registrar(...)` | `RegistrarConsumidorUseCase` | POST | `/api/consumidores` | `RegistrarConsumidorRequest` | `201` + `ConsumidorDetalleResponse` |
| `verificarPuedeComprar(...)` y `registrarCompraExitosa()` | `ConfirmarCompraDeBebidaUseCase` | POST | `/api/compras` | `ConfirmarCompraRequest` | `204` |

---

## 2. Mapeo de errores del dominio a códigos HTTP

| Excepción | Cuándo ocurre | Código HTTP |
|---|---|---|
| `ReglaDominioException` | Se intenta romper una regla de negocio | `400 Bad Request` |
| `NoSuchElementException` | El recurso (por id) no existe | `404 Not Found` |
| Error de validación del DTO (`@NotBlank`, `@Positive`, …) | El formato de la petición es inválido, antes de llegar al dominio | `400 Bad Request` |

---

## 3. DTOs de entrada (Request)

### 3.1 `RegistrarBebidaRequest`
**Mapea a:** `Bebida.publicar(...)` · **Estado:** programado

| Campo | Tipo | Validación | ¿Por qué es necesario? |
|---|---|---|---|
| `vendedorId` | `UUID` | `@NotNull` | Toda bebida debe pertenecer a un vendedor (regla del agregado Bebida). |
| `nombre` | `String` | `@NotBlank` | Es lo que el consumidor ve en el catálogo. |
| `material` | `MaterialEnvase` | `@NotNull` | Define las reglas del envase (si puede retornarse y cuántos usos tiene). |
| `capacidadMl` | `int` | `@Positive` | Capacidad del envase en mililitros; el dominio exige que sea mayor que cero. |
| `retornable` | `boolean` | — | Indica si el envase entra al ciclo de retorno. El dominio rechaza el aluminio retornable. |
| `grupoAlcoholico` | `GrupoAlcoholico` | `@NotNull` | Determina si la compra exige mayoría de edad. |
| `monto` | `double` | `@Positive` | Precio de venta; debe ser mayor que cero. |
| `moneda` | `String` | `@NotBlank`, `@Size(min=3, max=3)` | Código de moneda (por ejemplo `COP`). |


### 3.2 `CambiarPrecioRequest`
**Mapea a:** `Bebida.cambiarPrecio(...)` · **Estado:** programado

| Campo | Tipo | Validación | ¿Por qué es necesario? |
|---|---|---|---|
| `monto` | `double` | `@Positive` | El nuevo precio; el dominio exige que sea distinto del actual. |
| `moneda` | `String` | `@NotBlank`, `@Size(min=3, max=3)` | Debe coincidir con la moneda actual; el dominio rechaza el cambio de moneda. |


### 3.3 `RegistrarEnvaseRequest`
**Mapea a:** `Envase.registrar(...)` · **Estado:** solo diseñado

| Campo | Tipo | Validación | ¿Por qué es necesario? |
|---|---|---|---|
| `material` | `MaterialEnvase` | `@NotNull` | Define el límite de usos y si puede ser retornable. |
| `capacidadMl` | `int` | `@Positive` | Capacidad del envase en mililitros. |
| `retornable` | `boolean` | — | Indica si el envase se devuelve y reutiliza. |


### 3.4 `RegistrarConsumidorRequest`
**Mapea a:** `Consumidor.registrar(...)` · **Estado:** programado

| Campo | Tipo | Validación | ¿Por qué es necesario? |
|---|---|---|---|
| `nombre` | `String` | `@NotBlank` | Identifica a la persona registrada. |
| `codigo` | `String` | `@NotBlank`, `@Size(min=6, max=6)` | Código de acceso al catálogo; debe ser único. |
| `fechaNacimiento` | `LocalDate` | `@NotNull`, `@Past` | Permite calcular la mayoría de edad para aplicar la regla de alcohol. |


### 3.5 `ConfirmarCompraRequest`
**Mapea a:** `Consumidor.verificarPuedeComprar(...)` + `registrarCompraExitosa()` · **Estado:** programado

| Campo | Tipo | Validación | ¿Por qué es necesario? |
|---|---|---|---|
| `consumidorId` | `UUID` | `@NotNull` | Quién compra; de su edad e historial dependen las reglas. |
| `bebidaId` | `UUID` | `@NotNull` | Qué bebida compra; de ella se obtiene el grupo alcohólico y su disponibilidad. |
| `unidades` | `int` | `@Min(1)` | Cantidad comprada; el dominio valida la regla de volumen. |

---

## 4. DTOs de salida (Response)

### 4.1 `BebidaDetalleResponse`
**Se obtiene de:** `Bebida` · **Estado:** programado

| Campo | Tipo | ¿Por qué se expone? |
|---|---|---|
| `id` | `UUID` | Para que el cliente pueda referirse a la bebida en peticiones posteriores. |
| `vendedorId` | `UUID` | Quién la publicó. |
| `nombre` | `String` | Para mostrarla en el catálogo. |
| `material`, `capacidadMl`, `retornable` | `MaterialEnvase`, `int`, `boolean` | Descripción del envase, **aplanada** desde `TipoEnvase`. |
| `grupoAlcoholico` | `GrupoAlcoholico` | Para advertir si exige mayoría de edad. |
| `monto`, `moneda` | `double`, `String` | Precio, **aplanado** desde `Precio`. |
| `estado` | `EstadoBebida` | Para saber si está publicada o eliminada. |


### 4.2 `EnvaseDetalleResponse`
**Se obtiene de:** `Envase` · **Estado:** programado

| Campo | Tipo | ¿Por qué se expone? |
|---|---|---|
| `id` | `UUID` | Identifica al envase en peticiones posteriores. |
| `material`, `capacidadMl`, `retornable` | `MaterialEnvase`, `int`, `boolean` | Descripción del envase, aplanada desde `TipoEnvase`. |
| `estado` | `EstadoEnvase` | Nuevo, retornado, dañado o descartado. |
| `cantidadUsos` | `int` | Usos acumulados. |
| `usosRestantes` | `int` | Dato **calculado** por el dominio (`Envase.usosRestantes()`); no se guarda. |

### 4.3 `ConsumidorDetalleResponse`
**Se obtiene de:** `Consumidor` · **Estado:** solo diseñado

| Campo | Tipo | ¿Por qué se expone? |
|---|---|---|
| `id` | `UUID` | Identifica al consumidor. |
| `nombre` | `String` | Para mostrarlo. |
| `codigo` | `String` | Su código de acceso. |
| `comprasHistoricas` | `int` | Para que el consumidor vea su historial. |

---

## 5. Resumen de DTOs

| DTO | Tipo | Estado |
|---|---|---|
| `RegistrarBebidaRequest` | Request | Programado |
| `CambiarPrecioRequest` | Request | Programado |
| `RegistrarConsumidorRequest` | Request | Programado |
| `ConfirmarCompraRequest` | Request | Programado |
| `RegistrarEnvaseRequest` | Request | Solo diseñado |
| `BebidaDetalleResponse` | Response | Programado |
| `EnvaseDetalleResponse` | Response | Programado |
| `ConsumidorDetalleResponse` | Response | Solo diseñado |

