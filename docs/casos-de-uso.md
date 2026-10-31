# Casos de uso

**Proyecto:** Vent2Bebidas

**Integrantes:** Juan David Torres Arango · Oscar Leandro Agudelo Franco · Jorge Luis Ocampo Ocampo
---

## 1. Resumen

| ID | Caso de uso | Actor | Agregado | Repository que necesita |
|---|---|---|---|---|
| CU-01 | `RegistrarBebidaUseCase` | Vendedor | Bebida | `BebidaRepository` |
| CU-02 | `CambiarPrecioBebidaUseCase` | Vendedor | Bebida | `BebidaRepository` |
| CU-03 | `EliminarBebidaUseCase` | Vendedor | Bebida | `BebidaRepository` |
| CU-04 | `RegistrarEnvaseUseCase` | Vendedor | Envase | `EnvaseRepository` |
| CU-05 | `RegistrarRetornoEnvaseUseCase` | Vendedor | Envase | `EnvaseRepository` |
| CU-06 | `MarcarEnvaseComoDanadoUseCase` | Vendedor | Envase | `EnvaseRepository` |
| CU-07 | `DescartarEnvaseUseCase` | Vendedor | Envase | `EnvaseRepository` |
| CU-08 | `RegistrarConsumidorUseCase` | Consumidor | Consumidor | `ConsumidorRepository` |
| CU-09 | `ListarBebidasDisponiblesUseCase` | Consumidor | Bebida | `BebidaRepository` |
| CU-10 | `ConfirmarCompraDeBebidaUseCase` | Consumidor | Consumidor y Bebida | `ConsumidorRepository` y `BebidaRepository` |

**Cobertura:** 7 casos de uso del lado del vendedor y 3 del lado del consumidor.

---

## 2. Matriz casos de uso × repositorios

| Caso de uso | `BebidaRepository` | `EnvaseRepository` | `ConsumidorRepository` |
|---|:---:|:---:|:---:|
| CU-01 RegistrarBebida | ✔ | | |
| CU-02 CambiarPrecioBebida | ✔ | | |
| CU-03 EliminarBebida | ✔ | | |
| CU-04 RegistrarEnvase | | ✔ | |
| CU-05 RegistrarRetornoEnvase | | ✔ | |
| CU-06 MarcarEnvaseComoDanado | | ✔ | |
| CU-07 DescartarEnvase | | ✔ | |
| CU-08 RegistrarConsumidor | | | ✔ |
| CU-09 ListarBebidasDisponibles | ✔ | | |
| CU-10 ConfirmarCompraDeBebida | ✔ | | ✔ |

---

## 3. Detalle de cada caso de uso

### CU-01 — Registrar bebida
- **Actor:** Vendedor
- **Qué hace:** publica una bebida nueva en el catálogo.
- **Entrada:** `vendedorId`, `nombre`, `TipoEnvase`, `GrupoAlcoholico`, `Precio`
- **Salida:** la `Bebida` publicada
- **Operación del dominio:** `Bebida.publicar(...)`
- **Reglas que se disparan:** RN-01 (si el tipo es de aluminio retornable), RN-06
- **Errores:** `ReglaDominioException` si falta algún dato o el precio no es válido
- **Pruebas:** `BebidaUseCasesTest.debeRegistrarUnaBebidaDisponibleYGuardarla`

### CU-02 — Cambiar precio de una bebida
- **Actor:** Vendedor
- **Qué hace:** cambia el precio de una bebida existente.
- **Entrada:** `bebidaId`, nuevo `Precio`
- **Salida:** ninguna
- **Operación del dominio:** `Bebida.cambiarPrecio(...)`
- **Reglas que se disparan:** RN-06, RN-07
- **Errores:** `NoSuchElementException` si la bebida no existe; `ReglaDominioException` si el precio es igual al actual, cambia de moneda o la bebida está eliminada
- **Pruebas:** `BebidaUseCasesTest.debeCambiarElPrecioYGuardarElCambio`, `debeFallarAlCambiarElPrecioDeUnaBebidaQueNoExiste`

### CU-03 — Eliminar bebida
- **Actor:** Vendedor
- **Qué hace:** retira una bebida del catálogo de forma lógica; su historial se conserva.
- **Entrada:** `bebidaId`
- **Salida:** ninguna
- **Operación del dominio:** `Bebida.eliminarLogicamente()`
- **Reglas que se disparan:** RN-07
- **Errores:** `NoSuchElementException` si no existe; `ReglaDominioException` si ya estaba eliminada
- **Pruebas:** `BebidaUseCasesTest.debeEliminarLogicamenteLaBebidaSinBorrarla`

### CU-04 — Registrar envase
- **Actor:** Vendedor
- **Qué hace:** da de alta un envase nuevo para controlar su ciclo de vida.
- **Entrada:** `TipoEnvase` (material, capacidad, retornable)
- **Salida:** el `Envase` registrado, en estado `NUEVO` con 0 usos
- **Operación del dominio:** `Envase.registrar(...)`
- **Reglas que se disparan:** RN-01
- **Errores:** `ReglaDominioException` si el tipo es nulo
- **Pruebas:** `RegistrarEnvaseUseCaseTest.debeRegistrarUnEnvaseNuevoYGuardarlo`, `noDebeRegistrarUnEnvaseSinTipo`

### CU-05 — Registrar retorno de un envase
- **Actor:** Vendedor
- **Qué hace:** registra que un envase retornable fue devuelto y vuelve a circulación.
- **Entrada:** `envaseId`
- **Salida:** ninguna
- **Operación del dominio:** `Envase.registrarRetorno()`
- **Reglas que se disparan:** RN-02, RN-03, RN-04, RN-05
- **Errores:** `NoSuchElementException` si no existe; `ReglaDominioException` si no es retornable, está dañado, está descartado o agotó sus usos
- **Pruebas:** `RegistrarRetornoEnvaseUseCaseTest` (3 pruebas)

### CU-06 — Marcar envase como dañado
- **Actor:** Vendedor
- **Qué hace:** registra que un envase se dañó y ya no puede retornarse.
- **Entrada:** `envaseId`
- **Salida:** ninguna
- **Operación del dominio:** `Envase.marcarComoDanado()`
- **Reglas que se disparan:** RN-04, RN-05
- **Errores:** `NoSuchElementException` si no existe; `ReglaDominioException` si ya está descartado o ya estaba dañado
- **Pruebas:** `GestionarEstadoEnvaseUseCasesTest.debeMarcarElEnvaseComoDanadoYGuardarlo`, `noDebeMarcarComoDanadoUnEnvaseYaDescartado`, `debeFallarSiElEnvaseAMarcarNoExiste`

### CU-07 — Descartar envase
- **Actor:** Vendedor
- **Qué hace:** saca un envase de circulación de forma definitiva.
- **Entrada:** `envaseId`
- **Salida:** ninguna
- **Operación del dominio:** `Envase.descartar()`
- **Reglas que se disparan:** RN-05
- **Errores:** `NoSuchElementException` si no existe; `ReglaDominioException` si ya estaba descartado
- **Pruebas:** `GestionarEstadoEnvaseUseCasesTest.debeDescartarElEnvaseYGuardarlo`

### CU-08 — Registrar consumidor
- **Actor:** Consumidor
- **Qué hace:** registra a una persona para que pueda comprar.
- **Entrada:** `nombre`, `CodigoConsumidor`, `fechaNacimiento`
- **Salida:** el `Consumidor` registrado, con 0 compras históricas
- **Operación del dominio:** `Consumidor.registrar(...)`
- **Reglas que se disparan:** RN-10
- **Errores:** `ReglaDominioException` si el código ya existe, el nombre está vacío o la fecha de nacimiento es futura
- **Nota:** la unicidad del código se valida en el caso de uso porque exige consultar a todos los consumidores (ver `agregados-e-invariantes.md`, sección 6).
- **Pruebas:** `RegistrarConsumidorUseCaseTest` (2 pruebas)

### CU-09 — Listar bebidas disponibles
- **Actor:** Consumidor
- **Qué hace:** muestra el catálogo con las bebidas publicadas (no incluye las eliminadas).
- **Entrada:** ninguna
- **Salida:** lista de `Bebida`
- **Operación del dominio:** consulta (`Bebida.estaDisponible()` mediante el repositorio)
- **Reglas que se disparan:** RN-07
- **Errores:** ninguno
- **Pruebas:** `BebidaUseCasesTest.listarDisponiblesNoDebeIncluirLasBebidasEliminadas`

### CU-10 — Confirmar compra de bebida
- **Actor:** Consumidor
- **Qué hace:** confirma la compra de una cantidad de unidades de una bebida y la suma al historial del consumidor.
- **Entrada:** `consumidorId`, `bebidaId`, `unidades`
- **Salida:** ninguna
- **Operaciones del dominio:** `Bebida.verificarQueEstaDisponible()`, `Consumidor.verificarPuedeComprar(...)` y `Consumidor.registrarCompraExitosa()`
- **Reglas que se disparan:** RN-07, RN-08, RN-09
- **Errores:** `NoSuchElementException` si el consumidor o la bebida no existen; `ReglaDominioException` si la bebida no está disponible, el consumidor es menor y la bebida es alcohólica, o la compra por volumen no está permitida
- **Detalle importante:** la `Bebida` solo se **lee**; únicamente se modifica y guarda el `Consumidor`.
- **Pruebas:** `ConfirmarCompraDeBebidaUseCaseTest` (4 pruebas)

---
