# Clasificación Entidad / Value Object

**Proyecto:** Vent2Bebidas 

---

## 1. Resumen de la clasificación

| Concepto | Clasificación | Tipo Java |
|---|---|---|
| `Bebida` | **Entidad** | `class` |
| `Envase` | **Entidad** | `class` |
| `Consumidor` | **Entidad** | `class` | 
| `Precio` | **Value Object** | `record` | 
| `Capacidad` | **Value Object** | `record` | 
| `TipoEnvase` | **Value Object** | `record` | 
| `CodigoConsumidor` | **Value Object** | `record` |
| `MaterialEnvase` | **Value Object** | `enum` | 
| `EstadoEnvase` | **Value Object** | `enum` | 
| `GrupoAlcoholico` | **Value Object** | `enum` | 
| `EstadoBebida` | **Value Object** | `enum` | 

---

## 2. Las 3 entidades

### 2.1 `Bebida` — Entidad (raíz del agregado Bebida)

- **Identidad:** dos bebidas con el mismo `id` son la misma aunque cambien su nombre o su precio. Dos bebidas con igual nombre y precio pero distinto `id` son dos publicaciones diferentes.
  *Evidencia:* `BebidaTest.dosBebidasConLaMismaIdentidadSonLaMisma`.
- **Reemplazo:** `cambiarPrecio()` modifica el estado interno; la bebida sigue siendo la misma.
- **Ciclo de vida:** nace `PUBLICADA` y puede pasar a `ELIMINADA` (borrado lógico). Se busca por `id` en `BebidaRepository`.
- **Conclusión:** Entidad.

### 2.2 `Envase` — Entidad (raíz del agregado Envase)

- **Identidad:** cada envase es un objeto físico individual. Dos envases de vidrio de 330 ml retornables tienen los mismos datos, pero uno puede tener 3 usos y el otro 29. Son cosas distintas.
  *Evidencia:* `EnvaseTest.dosEnvasesConLaMismaIdentidadSonElMismo`.
- **Reemplazo:** `registrarRetorno()` suma un uso y cambia el estado del mismo envase; no se crea otro.
- **Ciclo de vida:** `NUEVO → RETORNADO → DANADO → DESCARTADO`, con `cantidadUsos` acumulando su historia. Se busca por `id` en `EnvaseRepository`.
- **Conclusión:** Entidad.

### 2.3 `Consumidor` — Entidad (raíz del agregado Consumidor)

- **Identidad:** dos consumidores con el mismo nombre y la misma fecha de nacimiento siguen siendo dos personas distintas.
  *Evidencia:* `ConsumidorTest.dosConsumidoresConLaMismaIdentidadSonElMismo`.
- **Reemplazo:** `registrarCompraExitosa()` modifica el mismo consumidor (su historial crece).
- **Ciclo de vida:** se registra, acumula `comprasHistoricas` y se busca por `id` en `ConsumidorRepository`.
- **Conclusión:** Entidad.

---

## 3. Los 8 Value Objects

### 3.1 Records (VOs con datos y validación)

#### `Precio(double monto, String moneda)`
- **Identidad:** dos precios de 5000 COP son intercambiables. *Evidencia:* `PrecioTest.dosPreciosConElMismoValorDebenSerIguales`.
- **Reemplazo:** `conDescuento(20)` devuelve un `Precio` nuevo y el original queda intacto. *Evidencia:* `PrecioTest.conDescuentoDebeReducirElMontoSinModificarElOriginal`.
- **Ciclo de vida:** no existe fuera de una `Bebida`; no tiene Repository.
- **Validación en el constructor:** monto mayor que cero y moneda de 3 letras.

#### `Capacidad(int mililitros)`
- **Identidad:** dos capacidades de 330 ml son lo mismo. *Evidencia:* `CapacidadTest.dosCapacidadesConElMismoValorDebenSerIguales`.
- **Reemplazo:** si un envase fuera de otra capacidad, sería otro tipo de envase con otra `Capacidad`, no una capacidad editada.
- **Ciclo de vida:** vive dentro de `TipoEnvase`.
- **Validación en el constructor:** debe ser mayor que cero.

#### `TipoEnvase(MaterialEnvase material, Capacidad capacidad, boolean retornable)`
- **Identidad:** dos tipos con los mismos tres datos son el mismo tipo. *Evidencia:* `TipoEnvaseTest.dosTiposConLosMismosValoresDebenSerIguales`.
- **Reemplazo:** no se edita; se describe otro tipo.
- **Ciclo de vida:** vive dentro de `Bebida` y de `Envase`; no tiene Repository.
- **Validación en el constructor:** material y capacidad obligatorios, y un envase de aluminio no puede ser retornable. *Evidencia:* `TipoEnvaseTest.noDebeCrearUnTipoRetornableDeAluminio`.

#### `CodigoConsumidor(String valor)`
- **Identidad:** dos códigos `AB12CD` son el mismo código. *Evidencia:* `CodigoConsumidorTest.dosCodigosConElMismoValorDebenSerIguales`.
- **Reemplazo:** un código no se edita; se asignaría otro.
- **Ciclo de vida:** vive dentro de `Consumidor`.
- **Validación en el constructor:** exactamente 6 caracteres alfanuméricos, normalizados a mayúsculas.

### 3.2 Enums (VOs con un conjunto cerrado de valores)

#### `MaterialEnvase` (`VIDRIO`, `PLASTICO`, `ALUMINIO`)
- **Identidad:** `VIDRIO` es siempre el mismo `VIDRIO`; no hay "dos vidrios distintos".
- **Reemplazo:** se cambia un material por otro, no se edita.
- **Ciclo de vida:** no nace ni muere; es un valor del catálogo cerrado.
- **Comportamiento propio:** `permiteRetorno()` y `usosMaximos()` (vidrio 30, plástico 10, aluminio 0).

#### `EstadoEnvase` (`NUEVO`, `RETORNADO`, `DANADO`, `DESCARTADO`)
- **Identidad:** `RETORNADO` es el mismo valor para todos los envases.
- **Reemplazo:** el envase cambia de estado asignándole otro valor.
- **Ciclo de vida:** el que tiene ciclo de vida es el `Envase`, no el estado en sí.
- **Comportamiento propio:** `puedeTransicionarA(...)` y `esFinal()`. *Evidencia:* `EstadoEnvaseTest.unEnvaseDescartadoEsFinalYNoAdmiteNingunaTransicion`.

#### `GrupoAlcoholico` (`SIN_ALCOHOL`, `FERMENTADA`, `DESTILADA`)
- **Identidad:** `FERMENTADA` es siempre la misma categoría.
- **Reemplazo:** una bebida pertenece a un grupo; no se edita el grupo.
- **Ciclo de vida:** es una clasificación fija.
- **Comportamiento propio:** `requiereMayoriaEdad()` y `getPorcentajeAlcoholBase()`.

#### `EstadoBebida` (`PUBLICADA`, `ELIMINADA`)
- **Identidad:** `ELIMINADA` es el mismo valor para todas las bebidas.
- **Reemplazo:** la bebida pasa de un estado al otro por asignación.
- **Ciclo de vida:** lo tiene la `Bebida`, no el valor.
- **Comportamiento propio:** `esFinal()`.

---


## 4. Conceptos considerados que NO son entidades del modelo

| Concepto | Decisión | Razón |
|---|---|---|
| **Vendedor** | No es entidad en esta entrega; `Bebida` guarda solo `vendedorId` | Pertenece a otro agregado. Los agregados se referencian por id, no por objeto. |
| **Retorno** | No es entidad; es la operación `Envase.registrarRetorno()` | Un retorno no se busca ni evoluciona por sí solo. Su efecto queda en `cantidadUsos` y en `estado`. |
| **Compra** | No es entidad; se modela como `Consumidor.registrarCompraExitosa()` | En esta entrega solo importa el historial de compras para aplicar la regla de volumen. |

---


